package com.vserve.project.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmbeddingClient {

    @Value("${app.ai.embedding.url}")
    private String url;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Double> getEmbedding(String text) {

        Map<String, String> body = new HashMap<>();
        body.put("text", text);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, body, Map.class);

        return (List<Double>) response.getBody().get("embedding");
    }
}