package com.capstone.demo.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Client for the Aynak AI Agent Service (the Python FastAPI app).
 * The ONLY place in the whole backend that knows the AI service exists.
 */
@Component
public class AiAgentClient {

    private final RestTemplate restTemplate;

    @Value("${ai.service.url:http://localhost:8000}")
    private String aiServiceUrl;

    public AiAgentClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Sends the report image + location to the AI pipeline.
     * Returns the raw JSON as a Map (keys: is_duplicate, category, urgency,
     * objects_detected, faces_blurred, blurred_image_base64, ...).
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> analyze(MultipartFile image,
                                       String latitude,
                                       String longitude,
                                       String description) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            ByteArrayResource imageResource =
                    new ByteArrayResource(image.getBytes()) {
                        @Override
                        public String getFilename() {
                            return image.getOriginalFilename() != null
                                    ? image.getOriginalFilename() : "report.jpg";
                        }
                    };

            body.add("image", imageResource);
            body.add("latitude", latitude);
            body.add("longitude", longitude);
            if (description != null) {
                body.add("description", description);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    aiServiceUrl + "/analyze",
                    new HttpEntity<>(body, headers),
                    Map.class
            );

            return response.getBody();

        } catch (IOException e) {
            throw new RuntimeException("Could not read uploaded image", e);
        } catch (Exception e) {
            // AI service down or errored — caller decides what to do
            throw new RuntimeException("AI service unavailable: " + e.getMessage(), e);
        }
    }
}
