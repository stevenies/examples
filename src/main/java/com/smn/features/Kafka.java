package com.smn.features;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.*;

public class Kafka {

    public static class Thermostat {
 
        private String name;
        private int temperature;

        public Thermostat() {} // Default constructor for Jackson

        public Thermostat(String name) {
            this.name = name;
            this.temperature = 0;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getTemperature() {
            return temperature;
        }

        public void setTemperature(int temperature) {
            this.temperature = temperature;
        }

        public String toJson() {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.writeValueAsString(this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public static Thermostat fromJson(String json) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(json, Thermostat.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final String BOOTSTRAP_SERVERS = "34.195.186.209:9094";
    private static final String TOPIC_NAME = "thermostat-readings";

    public static void main(String[] args) {
        System.out.println("Example of using Apache Kafka to implement a pub/sub pattern:");
        System.out.println("First, make sure Kafka broker is running on AWS and the topic '" + TOPIC_NAME + "' is created.");

        // Create Kafka producer and consumer instances
        KafkaProducer<String, String> producer = createProducer();
        KafkaConsumer<String, String> consumer = createConsumer();

        // Add shutdown hook to clean up resources on application exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            producer.close();
            consumer.close();
        }));

        // Create thermostat instances for producer tasks
        Thermostat thermostatUpstairs = new Thermostat("Upstairs");
        Thermostat thermostatDownstairs = new Thermostat("Downstairs");

        // Generate data and send it to Kafka
        CompletableFuture.runAsync(() -> rampTemp(thermostatUpstairs, 10, 1000L, producer));
        CompletableFuture.runAsync(() -> rampTemp(thermostatDownstairs, 5, 750L, producer));

        // Simulate a consumer that processes data produced by the producer threads
        CompletableFuture<Void> consumerFuture = CompletableFuture.runAsync(() -> {
             try {
 
                // Connect to Kafka and subscribe to the topic
                consumer.subscribe(Arrays.asList(TOPIC_NAME));
            
                // Continuously consume Kafka messages
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                    for (ConsumerRecord<String, String> record : records) {
                        Thermostat thermostat = Thermostat.fromJson(record.value());
                        System.out.println(
                            "    Consumer: Received thermostat " + 
                            thermostat.getName() + " with temperature " + 
                            thermostat.getTemperature() + " from partition " + 
                            record.partition() + " at offset " + record.offset());
                    }
                }

            } catch (Exception e) {
                System.err.println("Consumer error: " + e.getMessage());
            } finally {
                consumer.close();
            }
        });
        
        // Wait for the consumer thread to complete before exiting
        consumerFuture.join();
    }

    private static KafkaProducer<String, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        return new KafkaProducer<>(props);
    }

    private static KafkaConsumer<String, String> createConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "thermostat-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
        return new KafkaConsumer<>(props);
    }

    private static void rampTemp(Thermostat thermostat, int increment, long delay, KafkaProducer<String, String> producer) {
        final int maxTemp = 40;
        IntStream.iterate(0, i -> i + increment).limit(maxTemp / increment).forEach(i -> setTemp(thermostat, i, delay, producer));  // Ramp up
        IntStream.iterate(maxTemp, i -> i - increment).limit(maxTemp / increment).forEach(i -> setTemp(thermostat, i, delay, producer));  // Ramp down
    }

    private static void setTemp(Thermostat thermostat, int temp, long delay, KafkaProducer<String, String> producer) {
        try {
            thermostat.setTemperature(temp);
            System.out.println("Producer: Thermostat " + thermostat.getName() + " has been set to " + thermostat.getTemperature() + " degrees");
            
            // Send message to Kafka
            ProducerRecord<String, String> record = new ProducerRecord<>(
                TOPIC_NAME, 
                thermostat.getName(), 
                thermostat.toJson()
            );
            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    System.out.println(
                        "    Sent message to Kafka - Topic: " + metadata.topic() + 
                        ", Partition: " + metadata.partition() + 
                        ", Offset: " + metadata.offset());
                } else {
                    System.err.println("Failed to send message: " + exception.getMessage());
                }
            });

            // Simulate time-consuming task
            Thread.sleep(delay); 

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
