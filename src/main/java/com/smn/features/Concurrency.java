package com.smn.features;

import java.util.concurrent.*;

public class Concurrency {

    // Create a pool of threads to use for concurrent tasks
    static Executor threadPool = Executors.newFixedThreadPool(4);

    public static void main(String[] args) {
        System.out.println("Examples of using concurrency features in Java 8:");
    }
    
}
