package com.nexaride.booking_service.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class GeoService {

    public double[] getCoordinates(String address) {

        try {
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);

            // ✅ FINAL CORRECT URL
            String url = "https://nominatim.openstreetmap.org/search?q="
                    + encodedAddress + "&format=json&limit=1";

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "NexaRide-App (support@nexaride.com)");
            headers.set("Accept", "application/json");

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // ✅ STATUS CHECK
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Geo API failed: " + response.getStatusCode());
            }

            String body = response.getBody();

            // ✅ SAFE NULL CHECK
            if (body == null || body.trim().isEmpty()) {
                throw new RuntimeException("Empty response from Geo API");
            }

            System.out.println("FINAL URL: " + url);
            System.out.println("RESPONSE BODY: " + body);

            // ✅ VALIDATION
            if (!body.contains("\"lat\"") || !body.contains("\"lon\"")) {
                throw new RuntimeException("Invalid Geo response: " + body);
            }

            // ✅ PARSING
            String lat = body.split("\"lat\":\"")[1].split("\"")[0];
            String lon = body.split("\"lon\":\"")[1].split("\"")[0];

            return new double[]{
                    Double.parseDouble(lat),
                    Double.parseDouble(lon)
            };

        } catch (Exception e) {
            e.printStackTrace(); // ✅ DEBUG
            throw new RuntimeException("Error fetching coordinates: " + e.getMessage());
        }
    }
}