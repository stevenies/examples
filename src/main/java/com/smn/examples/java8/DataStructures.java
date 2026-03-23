package com.smn.examples.java8;
    
import java.util.*;
import java.util.stream.*;

/**
 * This class demonstrates the use of various Java data structures.  The main Java data structures covered include:
 * List: ArrayList, LinkedList
 * Set: HashSet, LinkedHashSet, TreeSet
 * Map: HashMap, LinkedHashMap, TreeMap
 * Queue: LinkedList, PriorityQueue
 * Deque: ArrayDeque, LinkedList
 */
public class DataStructures {

    // Create a class for use as test data  
    static class Student implements Comparable<Student> {
        private final String name;
        private final int age;
        private final List<String> courses;

        public Student(String name, int age, List<String> courses) {
            this.name = name;
            this.age = age;
            this.courses = courses;
        }

        public String name() {
            return name;
        }

        public int age() {
            return age;
        }

        public List<String> courses() {
            return courses;
        }

        @Override
        public int compareTo(Student other) {
            return Integer.compare(this.age, other.age);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age, courses);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Student other = (Student) obj;
            return age == other.age &&
                   Objects.equals(name, other.name) &&
                   Objects.equals(courses, other.courses);
        }

    }

    // Create some test data
    static Student[] students = {
        new Student("Steve", 67, Arrays.asList("Math", "Physics")),
        new Student("Steve", 18, Arrays.asList("Math", "Physics")),  // Two students with the same name but different ages and courses
        new Student("Sheri", 64, Arrays.asList("Biology", "Riding")),
        new Student("Jenny", 44, Arrays.asList("Math", "Teaching")),
        new Student("Paul", 89, Arrays.asList("Math", "Electronics", "Raddars")),
    };


    public static void main(String[] args) {
        System.out.println("Examples of various Java data structures:");

        System.out.print("\nQuickly generate the first 10 powers of 2 using IntStream: ");
        int[] powersOfTwo = IntStream.iterate(1, n -> n * 2)
            .limit(10)
            .toArray();
        IntStream.of(powersOfTwo).forEach(e -> System.out.print(e + " "));
        System.out.println();

        System.out.println("\nCreate a sorted TreeMap of students by name:");
        Map<String, Student> studentMap = Stream.of(students).collect(Collectors.toMap(
            Student::name,  // Key mapper: use the student's name as the key
            s -> s,         // Value mapper: use the student object as the value
            (s1, s2) -> s1, // Merge function: in case of duplicate keys, keep the first student
            TreeMap::new)); // Supplier: create a new TreeMap
        studentMap.forEach((name, student) -> System.out.println(name + " is " + student.age() + " years old"));

        System.out.println("\nPriorityQueue of students with custom comparator (sorted by age):");
        // PriorityQueue<Student> queue = new PriorityQueue<>(64, (s1,s2) ->Integer.compare(s1.age(), s2.age()));
        PriorityQueue<Student> queue = new PriorityQueue<>(64, Comparator.comparingInt(Student::age));
        queue.addAll(Arrays.asList(students));
        while(!queue.isEmpty()) {
            Student s = queue.poll();
            System.out.println(s.name() + ", Age: " + s.age());
        }

        System.out.println("\nLRU Cache using LinkedHashMap:");
        final int cacheSize = 4;
        Map<String, String> cache = new LinkedHashMap<>(cacheSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return size() > cacheSize;
            }
        };

        cache.put("A", "Apple");
        cache.put("B", "Banana");
        cache.put("C", "Cherry");
        cache.put("D", "Date");
        System.out.println("Cache after adding 4 entries: " + cache);
 
        cache.get("A"); // Access A to make it recently used
        cache.put("E", "Elderberry"); // This should evict B, the least recently used entry
        System.out.println("Cache after adding E (evicting least recently used): " + cache);

      }
    
}

