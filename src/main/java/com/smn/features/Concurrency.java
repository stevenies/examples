package com.smn.features;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.*;

public class Concurrency {

    // Example of passing data between threads using CompletableFuture
    private static void passingDataBetweenThreads() {

        final long producerDelay = 1000L;
        final long consumerDelay = 3000L;
        final int queueSize = 2;

        // Create a blocking queue to hold data produced by the producer thread
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(queueSize);

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
 
        // Create a pool of two threads to use for the producer and consumer.
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Start the threads.
        CompletableFuture.runAsync(producer, executor);
        CompletableFuture<Void> consumerThread = CompletableFuture.runAsync(consumer, executor);

        // Wait for the consumerThread to finish
        consumerThread.join();
        System.out.println(timeSeconds() + " Example completed: Exiting...");
        executor.shutdown();
    }

    private static String timeSeconds() {
        long timeMillis = System.currentTimeMillis();
        return String.valueOf(timeMillis % 100000);
    }
    

    public static void main(String[] args) {
        System.out.println("Examples of using concurrency features in Java 8:");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n=== Select an example to run: ================================");
                System.out.println("0. Exit");
                System.out.println("1. Passing data between threads");
                switch (scanner.nextInt()) {
                    case 0 -> {
                        return;
                    }
                    case 1 -> passingDataBetweenThreads();
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }

 }
