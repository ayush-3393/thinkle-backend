package com.thinkle_backend.ai.prompts;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PromptBuilder {
    private final PromptService promptService;

    public PromptBuilder(PromptService promptService) {
        this.promptService = promptService;
    }

    public String generateWordPrompt() {
        return promptService.loadPromptTemplate("generate_word.txt");
    }

    public String generateHintPrompt(String solutionWord, String hintType) {
        String template = promptService.loadPromptTemplate("generate_hint.txt");
        return promptService.renderPrompt(template, Map.of(
                "word", solutionWord,
                "hintType", hintType
        ));
    }
}
