package com.smn.examples.java8;
    
import java.util.*;

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
        new Student("Steve", 67, List.of("Math", "Physics")),
        new Student("Steve", 18, List.of("Math", "Physics")),  // Two students with the same name but different ages and courses
        new Student("Sheri", 64, List.of("Biology", "Riding")),
        new Student("Jenny", 44, List.of("Math", "Teaching")),
        new Student("Paul", 89, List.of("Math", "Electronics", "Raddars")),
    };


    public static void main(String[] args) {
        System.out.println("Examples of various Java data structures:");

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

