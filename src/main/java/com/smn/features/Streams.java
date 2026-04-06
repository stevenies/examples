package com.smn.features;
    
import java.util.*;
import java.util.stream.*;

public class Streams {

    static record Student(String name, int age, List<String> courses) implements Comparable<Student> {

        // Compare two students first by name, then by age, but NOT by courses.
        @Override
        public int compareTo(Student other) {
            return Comparator.comparing(Student::name).thenComparingInt(Student::age).compare(this, other);
        }
    }

    // Create some test data.
    static Student[] students = {
        new Student("Steve", 67, List.of("Math", "Physics")),
        new Student("Steve", 18, List.of("Math", "Physics")),  // Two students with the same name but different ages and courses
        new Student("Sheri", 64, List.of("Biology", "Riding")),
        new Student("Jenny", 44, List.of("Math", "Teaching", "Classroom Management", "Education")),
        new Student("Paul", 89, List.of("Math", "Electronics", "Raddars")),
   };

    // Create a method to demonstrate the use of the anyMatch() method and variable scoping in lambda expressions.
    private static boolean foundMatch(String name) {
        return Stream.of(students).anyMatch(s -> s.name().equalsIgnoreCase(name));
    }

    public static void main(String[] args) {

        //--- JAVA 8 STREAM EXAMPLES ---
        System.out.println("Examples of using Java 8 streams for various operations on collections:");

        System.out.print("\nQuickly generate the first 10 powers of 2 using IntStream: ");
        int[] powersOfTwo = IntStream.iterate(1, n -> n * 2).limit(10).toArray();
        Arrays.stream(powersOfTwo).forEach(e -> System.out.print(e + " "));
        System.out.println();

        System.out.print("\nUse reduce to calculate the product of numbers from 1 to 10: ");
        List<Long> oneToTen = LongStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
        // Note that a combiner function (Long::sum) is not needed here because 1) an identity value is supplied,
        // and 2) the return type is the same as the source data.
        long product = oneToTen.stream().reduce(1L, (total, item) -> total * item);
        System.out.println(String.format("%,d", product));

        System.out.print("\nA student named Sheri is in the list of students: ");
        System.out.println(foundMatch("Sheri"));

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
            .sorted(Comparator.comparing((Student s) -> s.courses().size()).reversed())
            // The above line is simpler, but the following line demonstrates how to use .reduce function to count the number of courses for each student, which is more flexible if we wanted to do something more complex than just counting.
            // Note that the reduce operation needs a Combiner function (Long::sum) to change the type from Long to long, which is required by the Comparator.comparing method.
            // .sorted(Comparator.comparing((Student s) -> s.courses().stream().reduce(0L, (sum, course) -> sum + 1, Long::sum)).reversed())
            .forEach(s -> System.out.println(s.name() + " has " + s.courses().size() + " courses"));

        
        //--- JAVA 9 STREAM EXAMPLES ---
        System.out.println("\nExamples of using Java 9 streams for various operations on collections:");

        // The takeWhile method is more efficient than filter when you want to take elements from a stream until a certain condition is no longer met.
        // It is especially useful when working with sorted streams, as it can stop processing as soon as the condition is no longer satisfied.
        System.out.print("\nUse the takeWhile method to create a list of senior students: ");
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

