package com.smn.examples.java9;
    
import java.util.*;
import java.util.stream.*;

public class Streams {

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

    // Create some test data using the new List.of() method for immutable lists.
    static Student[] students = {
        new Student("Steve", 67, List.of("Math", "Physics")),
        new Student("Steve", 18, List.of("Math", "Physics")),  // Two students with the same name but different ages and courses
        new Student("Sheri", 64, List.of("Biology", "Riding")),
        new Student("Jenny", 44, List.of("Math", "Teaching", "Classroom Management", "Education")),
        new Student("Paul", 89, List.of("Math", "Electronics", "Raddars")),
    };

    // Create a method to demonstrate the use of the new .anyMatch() method and variable scoping in lambda expressions.
    private static boolean foundMatch(String name) {
        return Stream.of(students).anyMatch(s -> s.name().equalsIgnoreCase(name));
    }

    public static void main(String[] args) {
        System.out.println("Examples of using Java9 streams for various operations on collections:");

        System.out.print("\nIs a student named Sheri in the list of students: ");
        System.out.println(foundMatch("Sheri"));

        // The takeWhile method is more efficient than filter when you want to take elements from a stream until a certain condition is no longer met.
        // It is especially useful when working with sorted streams, as it can stop processing as soon as the condition is no longer satisfied.
        System.out.print("\nUse the takeWhile method to create a list of senior citizen students: ");
        List<String> seniorStudents = Stream.of(students)
            .sorted(Comparator.comparing(Student::age).reversed())
            .takeWhile(s -> s.age() >= 65)
            .map(Student::name).collect(Collectors.toList());
        seniorStudents.forEach(s -> System.out.print(s + " "));
        System.out.println();
    
        System.out.print("\nUse the dropWhile method to create a list of younger students: ");
        List<String> youngerStudents = Stream.of(students)
            .sorted(Comparator.comparing(Student::age).reversed())
            .dropWhile(s -> s.age() >= 65)
            .map(Student::name).collect(Collectors.toList());
        youngerStudents.forEach(s -> System.out.print(s + " "));
        System.out.println();
    
    }
}

