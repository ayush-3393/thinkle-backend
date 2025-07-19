package com.thinkle_backend.ai.features.replyToGuesses;

import com.thinkle_backend.ai.messageParser.AiMessageParser;
import com.thinkle_backend.ai.prompts.PromptBuilder;
import com.thinkle_backend.ai.services.AiService;
import com.thinkle_backend.models.enums.GameStatus;
import org.springframework.stereotype.Service;

@Service
public class GenerateReplyToGuessesViaGemini implements ReplyToGuessesGenerator{

    private final AiService aiService;
    private final AiMessageParser aiMessageParser;
    private final PromptBuilder promptBuilder;

    public GenerateReplyToGuessesViaGemini(AiService aiService,
                                           AiMessageParser aiMessageParser,
                                           PromptBuilder promptBuilder) {
        this.aiService = aiService;
        this.aiMessageParser = aiMessageParser;
        this.promptBuilder = promptBuilder;
    }

    @Override
    public String generateReplyToTheGuessedWord(String currentGuess,
                                                String wordToGuess,
                                                GameStatus gameStatus,
                                                Integer remainingLives,
                                                Long hintsUsed) {
        String geminiResponse = this.aiService.getAnswer(this.promptBuilder.generateReplyForCurrentGuess(
                currentGuess, wordToGuess, gameStatus, remainingLives, hintsUsed)
        );
        return this.aiMessageParser.extractText(geminiResponse);
    }
}
