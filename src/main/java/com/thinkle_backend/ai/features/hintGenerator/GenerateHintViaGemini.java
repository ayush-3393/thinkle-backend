package com.thinkle_backend.ai.features.hintGenerator;

import com.thinkle_backend.ai.messageParser.AiMessageParser;
import com.thinkle_backend.ai.prompts.PromptBuilder;
import com.thinkle_backend.ai.services.AiService;
import org.springframework.stereotype.Service;

@Service
public class GenerateHintViaGemini implements HintGenerator{

    private final AiService aiService;
    private final AiMessageParser aiMessageParser;
    private final PromptBuilder promptBuilder;

    public GenerateHintViaGemini(AiService aiService,
                                 AiMessageParser aiMessageParser,
                                 PromptBuilder promptBuilder) {
        this.aiService = aiService;
        this.aiMessageParser = aiMessageParser;
        this.promptBuilder = promptBuilder;
    }

    @Override
    public String generateHint(String solutionWord, String hintType) {
        String geminiResponse =
                this.aiService.getAnswer(this.promptBuilder.generateHintPrompt(solutionWord, hintType));
        return this.aiMessageParser.extractText(geminiResponse);
    }
}
