package com.smn.features;

import com.smn.utils.Http;
import java.util.*;
import java.util.concurrent.*;

public class Concurrency {

    // Example of passing data between CompletableFuture threads 
    private static void passingDataBetweenThreads() {

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

       // Create a list of websites to fetch content from. These should be simple endpoints that return text for easy word counting.
    static String[] websites = {
        "https://api-excellence.com",
        "https://www.cnn.com",
        "https://www.ietf.org/rfc/rfc2616.txt"
    };

    // Count the number of words contained within a collection of websites
    private static void countWordsManyWebsites() {

         // Create a CompletableFuture for each website to count the number of words
        List<CompletableFuture<Integer>> websiteMetrics = Arrays.stream(websites).map(url -> {
            return countWordsSingleWebsite(url);
        }).toList();

        // Wait for all futures to complete and sum the total word count
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(websiteMetrics.toArray(new CompletableFuture[0]));
        CompletableFuture<Void> summary = allFutures.thenRun(() -> {
            int totalWords = websiteMetrics.stream().mapToInt(future -> future.join()).sum();
            System.out.println(timeSeconds() + " Total words across all websites: " + totalWords);
        });

        // Wait for the summary to complete before exiting
        summary.join();
    }

    private static CompletableFuture<Integer> countWordsSingleWebsite(String url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println(timeSeconds() + " Fetching content from " + url);
                String content = Http.fetchWebContent(url);
                int wordCount = content.split("\\s+").length;
                System.out.println(timeSeconds() + " " + url + " has " + wordCount + " words");
                return wordCount;
            } catch (Exception e) {
                System.err.println(timeSeconds() + " Error fetching content from " + url + " - " + e.getMessage());
                return 0; // Return 0 words if there's an error
            }
        });
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
                System.out.println("0. Exit");
                System.out.println("1. Passing data between threads");
                System.out.println("2. Count words in single website");
                System.out.println("3. Count words in many websites");
                System.out.print("Choice: ");
                switch (scanner.nextInt()) {
                    case 0 -> {
                        return;
                    }
                    case 1 -> passingDataBetweenThreads();
                    case 2 -> countWordsSingleWebsite(websites[0]).thenAccept(wordCount -> {
                        System.out.println(timeSeconds() + " " + websites[0] + " has " + wordCount + " words");
                    });
                    case 3 -> countWordsManyWebsites();
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }

 }
