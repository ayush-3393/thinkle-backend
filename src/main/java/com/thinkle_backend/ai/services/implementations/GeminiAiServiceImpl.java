package com.thinkle_backend.ai.services.implementations;

import com.thinkle_backend.ai.services.AiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class GeminiAiServiceImpl implements AiService {


    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient;

    public GeminiAiServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String getAnswer(String question) {
        // Construct request payload
        Map<String, Object> requestPayload = Map.of(
                "contents", new Object[] {
                        Map.of(
                                "parts", new Object[] {
                                        Map.of(
                                                "text", question
                                        )
                                }
                        )
                }
        );

        Map<String, String> headerMap = Map.of(
                "Content-Type", "application/json",
                "X-goog-api-key", this.geminiApiKey
        );

        // Make Api Call
        return this.webClient.post()
                .uri(this.geminiApiUrl)
                .headers(httpHeaders -> headerMap.forEach(httpHeaders::add))
                .bodyValue(requestPayload)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
