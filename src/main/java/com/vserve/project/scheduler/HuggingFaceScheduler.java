package com.vserve.project.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Component
public class HuggingFaceScheduler {

    private final RestTemplate restTemplate;

    private static final String HEALTH_URL =
            "https://vservecommunity-ai-embedding-service.hf.space/";

    public HuggingFaceScheduler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Runs every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void keepAlive() {
        try {
            ResponseEntity<String> response =
                    restTemplate.getForEntity(HEALTH_URL, String.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
