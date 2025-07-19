package com.thinkle_backend.ai.messageParser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinkle_backend.ai.exceptions.AiMessageParserException;
import org.springframework.stereotype.Component;

@Component
public class GeminiMessageParser implements AiMessageParser{

    private final ObjectMapper objectMapper;

    public GeminiMessageParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String extractText(String aiResponseInJson) {
        try {
            JsonNode rootNode = objectMapper.readTree(aiResponseInJson);

            JsonNode candidatesNode = rootNode.get("candidates");
            if (candidatesNode != null && candidatesNode.isArray() && !candidatesNode.isEmpty()) {
                JsonNode firstCandidate = candidatesNode.get(0);
                JsonNode contentNode = firstCandidate.get("content");
                JsonNode partsNode = contentNode.get("parts");

                if (partsNode != null && partsNode.isArray() && !partsNode.isEmpty()) {
                    JsonNode firstPart = partsNode.get(0);
                    JsonNode textNode = firstPart.get("text");

                    if (textNode != null) {
                        return textNode.asText().trim();
                    }
                }
            }
            throw new AiMessageParserException("Failed to map the response");
        } catch (Exception e) {
            throw new AiMessageParserException("Failed to parse Gemini response");
        }
    }
}

