package com.thinkle_backend.ai.prompts;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

@Service
public class PromptService {
    public String loadPromptTemplate(String filename) {
        try {
            ClassPathResource resource = new ClassPathResource("prompts/" + filename);
            return Files.readString(resource.getFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load prompt: " + filename, e);
        }
    }

    public String renderPrompt(String template, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return template;
    }
}
