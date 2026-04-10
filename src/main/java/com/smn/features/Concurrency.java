package com.smn.features;

import com.smn.utils.Http;

import jakarta.validation.constraints.Size;

import java.util.*;
import java.util.concurrent.*;

import org.springframework.boot.autoconfigure.web.WebProperties.Resources.Chain.Strategy.Fixed;
import org.springframework.scheduling.annotation.Scheduled;

public class Concurrency {

    // Thread Pool Types:
    //      Single-thread executor: strict ordering on one worker. Perfect for serialised work. No parallelism.
    //      Fixed thread pool: stable thread count, unbounded queue by default. Great for CPU work when sized to cores. Watch for growing queues.
    //      Cached thread pool: grows on demand, shrinks when idle. Responsible for short I/O tasks. Dangerous under sustained flood.
    //      Work-stealing (ForkJoinPool): per-worker deques and stealing. Best for CPU-bound, fork/join style tasks. Avoid blocking.
    //      Scheduled thread pool: timers and periodic work. Size it to the concurrent tasks. Understand fixed-rate vs fixed-delay.
    //      Virtual threads (JDK 21+): cheap thread per task. Excellent for I/O heavy concurrency without giant platform-thread pools.

    // Example of chaining CompletableFuture stages together, passing data between them, and running them on different threads
    private static void doChainingExample() {
        System.out.println("Chaining Example - main thread is " + Thread.currentThread().getName());
        CompletableFuture<Void> thread = CompletableFuture.supplyAsync(() -> {   // SupplyAsync starts a new thread and returns a value
            doWork();
            System.out.println("Passing \"Steve\" to thenApplyAsync");
            return "Steve";
        }).thenApplyAsync(value -> {  // thenApplyAsync takes the result of the previous stage and returns a new value
            System.out.println("Received \"" + value + "\" in thenApplyAsync on Thread " + Thread.currentThread().getName());
            doWork();
            System.out.println("Passing \"Steve Nies\" to thenAcceptAsync");
            return value + " Nies";
        }).thenAcceptAsync(value -> {  // thenAcceptAsync takes the result of the previous stage and returns void
            System.out.println("Received \"" + value + "\" from thenApplyAsync on Thread " + Thread.currentThread().getName());
            doWork();
        });
        System.out.println("Waiting for threads to finish. Main thread is " + Thread.currentThread().getName());
        thread.join(); // Wait for the thread to complete before exiting
    }

    // Example of passing data between CompletableFuture threads 
    private static void doPassDataBetweenThreads() {

        final long producerDelay = 1000L;
        final long consumerDelay = 3000L;
        final int queueSize = 2;

        // Create a blocking queue to hold data produced by the producer thread
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(queueSize);

        // Simulate a producer that generates data
        Runnable producer = () -> {
            try {
                for (int i = 0; i < 10; i++) {
                    System.out.println(timeSeconds() + " Producer: Adding Item " + i);
                    queue.put("Data " + i); 
                    System.out.println(timeSeconds() + " Producer: Item " + i + " added");
                    Thread.sleep(producerDelay); // Simulate time-consuming task
                }

                // Inform the consumer that production is done by adding a special "end" item
                System.out.println(timeSeconds() + " Producer: Adding DONE flag");
                queue.put("DONE"); 
                System.out.println(timeSeconds() + " Producer: DONE flag added");

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // Simulate a consumer that processes data produced by the producer thread
        Runnable consumer = () -> {
            try {
                while (true) {
                    System.out.println("    " + timeSeconds() + " Consumer: Waiting for data...");
                    String data = queue.take();
                    System.out.println("    " + timeSeconds() + " Consumer: Received " + data);
                    Thread.sleep(consumerDelay); // Simulate time-consuming task

                    if ("DONE".equalsIgnoreCase(data)) {
                        System.out.println("    " + timeSeconds() + " Consumer: Exiting...");
                        break;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
 
        // Create a pool of threads to use for the producer and consumer.
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Start the threads.
        CompletableFuture.runAsync(producer, executor);
        CompletableFuture<Void> consumerThread = CompletableFuture.runAsync(consumer, executor);

        // Wait for the consumerThread to finish
        consumerThread.join();
        System.out.println(timeSeconds() + " Example completed: Exiting...");
        executor.shutdown();
    }

    private static void doCountWordsSingleWebsite() {
        CompletableFuture<Integer> thread = countWebsiteWords("https://api-excellence.com");
        thread.join();  // Wait for the thread to complete before exiting
    }

    private static void doCountWordsMultipleWebsites() {

        // Create a list of websites from which to fetch content.
        String[] websiteUrls = {
            "https://api-excellence.com",
            "https://www.cnn.com",
            "https://www.ietf.org/rfc/rfc2616.txt"
        };

         // Create a CompletableFuture for each website to count the number of words
        List<CompletableFuture<Integer>> metrics = Arrays.stream(websiteUrls).map(url -> {
            return countWebsiteWords(url);
        }).toList();

        // Wait for all futures to complete and sum the total word count
        CompletableFuture<Void> wrapper = CompletableFuture.allOf(
            metrics.toArray(new CompletableFuture[0]));  // Array is needed because allOf takes varargs.  Empty array is needed so that resulting array is typed
        CompletableFuture<Void> summary = wrapper.thenRun(() -> {   // Wrapper completes when all individual futures complete
            int totalWords = metrics.stream().mapToInt(future -> future.join()).sum();  // Join each future to get its value
            System.out.println(timeSeconds() + " Total words across all websites: " + totalWords);
        });

        // Wait for the summary to complete before exiting
        summary.join();
    }

    // Helper method to count words in a single website asynchronously
    private static CompletableFuture<Integer> countWebsiteWords(String url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println(timeSeconds() + " Fetching content from " + url);
                String content = Http.fetchContent(url);
                int wordCount = content.split("\\s+").length;
                System.out.println(timeSeconds() + " " + url + " has " + wordCount + " words");
                return wordCount;
            } catch (Exception e) {
                System.err.println(timeSeconds() + " Error fetching content from " + url + " - " + e.getMessage());
                return 0; // Return 0 words if there's an error
            }
        });
    }

    // Helper method to simulate time-consuming work
    private static void doWork() {
        try {
            for (int i = 0; i < 3; i++) {
                System.out.println(timeSeconds() + " Doing work " + (i + 1) + " on Thread " + Thread.currentThread().getName());
                Thread.sleep(1000); // Simulate time-consuming work
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Helper method to get the current time in seconds with milliseconds for better readability
    private static String timeSeconds() {
        String timeMillis = String.valueOf(System.currentTimeMillis());
        int length = timeMillis.length();
        return timeMillis.substring(length-6, length-4) + "." + timeMillis.substring(length-3);
    }

    public static void main(String[] args) {
        System.out.println("Examples of using concurrency features in Java 8:");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n=== Select an example to run: ================================");
                System.out.println("0: Exit");
                System.out.println("1: Passing data between threads");
                System.out.println("2: Chaining CompletableFutures");
                System.out.println("3: Count words in single website");
                System.out.println("4: Count words in multiple websites");
                System.out.print("Choice: ");

                int choice;
                try {
                    choice = scanner.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.nextLine(); // Clear the invalid input
                    continue;
                }

                switch (choice) {
                    case 0 -> {
                        return;
                    }
                    case 1 -> doPassDataBetweenThreads();
                    case 2 -> doChainingExample();
                    case 3 -> doCountWordsSingleWebsite();
                    case 4 -> doCountWordsMultipleWebsites();
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }

 }
