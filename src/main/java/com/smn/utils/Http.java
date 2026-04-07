package com.smn.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class Http {
    
    /**
     * Fetches the contents of a web URL and returns it as a String.
     * This method uses Java's HttpClient for modern HTTP handling.
     * 
     * @param url The URL to fetch content from
     * @return The content of the web page as a String
     * @throws Exception if there's an error fetching the content
     */
    public static String fetchWebContent(String url) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        return response.body();
    }

}
