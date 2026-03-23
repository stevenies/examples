package com.smn.examples.java8;
    
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

    // Create some test data
    static Student[] students = {
        new Student("Steve", 67, Arrays.asList("Math", "Physics")),
        new Student("Steve", 18, Arrays.asList("Math", "Physics")),  // Two students with the same name but different ages and courses
        new Student("Sheri", 64, Arrays.asList("Biology", "Riding")),
        new Student("Jenny", 44, Arrays.asList("Math", "Teaching", "Classroom Management", "Education")),
        new Student("Paul", 89, Arrays.asList("Math", "Electronics", "Raddars")),
   };


    public static void main(String[] args) {
        System.out.println("Examples of using Java 8 streams for various operations on collections:");

        System.out.print("\nQuickly generate the first 10 powers of 2 using IntStream: ");
        int[] powersOfTwo = IntStream.iterate(1, n -> n * 2)
            .limit(10)
            .toArray();
        IntStream.of(powersOfTwo).forEach(e -> System.out.print(e + " "));
        System.out.println();

        System.out.print("\nUse reduce to calculate the product of numbers from 1 to 10: ");
        List<Long> oneToTen = LongStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
        long product = oneToTen.stream().reduce(1L, (total, item) -> total * item);
        System.out.println(String.format("%,d", product));

        System.out.print("\nDetermine the age of the youngest student: ");
        int youngestAge = Arrays.stream(students)
            .mapToInt(Student::age)
            .min()  // Returns an OptionalInt, which may be empty if the stream is empty
            .orElse(0);
        System.out.println(youngestAge);

        System.out.println("\nFilter students older than 50, get their courses, and print them sorted:");
        Arrays.stream(students)
            .filter(s -> s.age() > 50)
            .flatMap(s -> s.courses().stream())
            .distinct()
            .sorted()
            .forEach(System.out::println);

        System.out.println("\nPrint students stored by who has the heaviest course workload:");
        Arrays.stream(students)
            // .sorted(Comparator.comparing(s -> s.courses().size()))
            // The above line is simpler, but the following line demonstrates how to use .reduce function to count the number of courses for each student, which is more flexible if we wanted to do something more complex than just counting.
            // Note that the reduce operation needs a Combiner function (Long::sum) to change the type from Long to long, which is required by the Comparator.comparing method.
            .sorted(Comparator.comparing((Student s) -> s.courses().stream().reduce(0L, (sum, course) -> sum + 1, Long::sum)).reversed())
            .forEach(s -> System.out.println(s.name() + " has " + s.courses().size() + " courses"));

      }
    
}

