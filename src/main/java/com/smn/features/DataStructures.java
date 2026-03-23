package com.smn.features;
    
import java.util.*;
import java.util.stream.*;

/**
 * This class demonstrates the use of various Java data structures.  The main Java data structures include:
 * List: ArrayList, LinkedList
 * Set: HashSet, LinkedHashSet, TreeSet
 * Map: HashMap, LinkedHashMap, TreeMap
 * Queue: LinkedList, PriorityQueue
 * Deque: ArrayDeque, LinkedList
 */
public class DataStructures {

    // Create a Java 8 class for use as test data  
    static class Student_J8 implements Comparable<Student_J8> {
        private final String name;
        private final int age;
        private final List<String> courses;

        public Student_J8(String name, int age, List<String> courses) {
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
        public int compareTo(Student_J8 other) {
            int comparison = this.name.compareTo(other.name);
            if (comparison != 0) {
                return comparison;
            }
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

    // Java 16 record for use as test data.  A record is a special type of class that is designed to be a simple data carrier.
    // Whereas a record automatically generates common methods it does NOT automatically generate a compareTo method.  
    static record Student(String name, int age, List<String> courses) implements Comparable<Student> {

        // Compare two students first by name, then by age, but NOT by courses.
        @Override
        public int compareTo(Student other) {
            return Comparator.comparing(Student::name).thenComparingInt(Student::age).compare(this, other);
        }
    }

    // Create some test data using Java 8.
    static Student_J8[] students_J8 = {
        new Student_J8("Steve", 67, Arrays.asList("Math", "Physics")),
        new Student_J8("Steve", 18, Arrays.asList("Math", "Physics")),  // Two students with the same name but different ages and courses
        new Student_J8("Sheri", 64, Arrays.asList("Biology", "Riding")),
        new Student_J8("Jenny", 44, Arrays.asList("Math", "Teaching", "Classroom Management", "Education")),
        new Student_J8("Paul", 89, Arrays.asList("Math", "Electronics", "Raddars")),
    };

    // Create some test data using Java 9 List.of() method.
    static Student[] students = {
        new Student("Steve", 67, List.of("Math", "Physics")),
        new Student("Steve", 18, List.of("Math", "Physics")),  // Two students with the same name but different ages and courses
        new Student("Sheri", 64, List.of("Biology", "Riding")),
        new Student("Jenny", 44, List.of("Math", "Teaching", "Classroom Management", "Education")),
        new Student("Paul", 89, List.of("Math", "Electronics", "Raddars")),
    };

    // Create a list of classes offered at the school.
    static List<String> classes = Stream.of(students).flatMap(s -> s.courses().stream()).distinct().toList();

    public static void main(String[] args) {
        System.out.println("Examples of various Java data structures:");

        System.out.print("\nQuickly generate the first 10 powers of 2 using IntStream: ");
        int[] powersOfTwo = IntStream.iterate(1, n -> n * 2).limit(10).toArray();
        IntStream.of(powersOfTwo).forEach(e -> System.out.print(e + " "));
        System.out.println();

        System.out.print("\nUse a Comparator to sort students by age (old to young): ");
        Arrays.stream(students)
            .sorted(Comparator.comparingInt(Student::age).thenComparing(Student::name).reversed())
            .forEach(s -> System.out.print(s.name() + " (" + s.age() + ") "));
        System.out.println();

        System.out.println("\nCreate a sorted TreeMap of students by name:");
        Map<String, Student> studentMap = Stream.of(students).collect(Collectors.toMap(
            Student::name,  // Key mapper: use the student's name as the key
            s -> s,         // Value mapper: use the student object as the value
            (s1, s2) -> s1, // Merge function: in case of duplicate keys, keep the first student
            TreeMap::new)); // Supplier: create a new TreeMap
        studentMap.forEach((name, student) -> System.out.println(name + " is " + student.age() + " years old"));

        System.out.println("\nLRU Cache using LinkedHashMap:");
        final int cacheSize = 4;
        final boolean useAccessOrder = true; // Set to true for access-order, false for insertion-order
        Map<String, String> cache = new LinkedHashMap<>(cacheSize, 0.75f, useAccessOrder) {
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

        System.out.println("\nPriority queue of students with custom comparator (sorted by age):");
        // PriorityQueue<Student> queue = new PriorityQueue<>(64, (s1,s2) ->Integer.compare(s1.age(), s2.age()));
        PriorityQueue<Student> queue = new PriorityQueue<>(64, Comparator.comparingInt(Student::age));
        queue.addAll(Arrays.asList(students));
        // Note that you can't use an iterator to remove items from a PriorityQueue because it doesn't guarantee
        // the element order.  You must use the poll() method to remove elements in the correct order.
        while(!queue.isEmpty()) {
            Student s = queue.poll();
            System.out.println(s.name() + ", Age: " + s.age());
        }

        System.out.println("\nLIFO stack using ArrayDeque:");
        Deque<String> stack = new ArrayDeque<>();
        stack.push("First");
        stack.push("Second");
        stack.push("Third");
        System.out.println("Stack after pushing 3 elements: " + stack);
        System.out.println("Popping from stack: " + stack.pop());
        System.out.println("Stack after popping: " + stack);

        System.out.println("\nFIFO queue using ArrayDeque:");
        Deque<String> fifoQueue = new ArrayDeque<>();
        fifoQueue.offer("First");
        fifoQueue.offer("Second");
        fifoQueue.offer("Third");
        System.out.println("Queue after adding 3 elements: " + fifoQueue);
        System.out.println("Polling from queue: " + fifoQueue.poll());
        System.out.println("Queue after polling: " + fifoQueue);

        System.out.println();
    }
}

