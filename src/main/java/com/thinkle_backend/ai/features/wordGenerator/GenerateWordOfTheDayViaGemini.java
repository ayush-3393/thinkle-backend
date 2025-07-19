package com.thinkle_backend.ai.features.wordGenerator;

import com.thinkle_backend.ai.messageParser.AiMessageParser;
import com.thinkle_backend.ai.prompts.PromptBuilder;
import com.thinkle_backend.ai.services.AiService;
import org.springframework.stereotype.Service;

@Service
public class GenerateWordOfTheDayViaGemini implements WordOfTheDayGenerator{

    private final AiService aiService;
    private final AiMessageParser aiMessageParser;
    private final PromptBuilder promptBuilder;

    public GenerateWordOfTheDayViaGemini(AiService aiService,
                                         AiMessageParser aiMessageParser,
                                         PromptBuilder promptBuilder) {
        this.aiService = aiService;
        this.aiMessageParser = aiMessageParser;
        this.promptBuilder = promptBuilder;
    }

    @Override
    public String generateWordOfTheDay() {
        String geminiResponse = this.aiService.getAnswer(this.promptBuilder.generateWordPrompt());
        return this.aiMessageParser.extractText(geminiResponse);
    }
}
