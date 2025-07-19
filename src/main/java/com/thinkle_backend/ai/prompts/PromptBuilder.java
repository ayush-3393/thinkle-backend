package com.thinkle_backend.ai.prompts;

import com.thinkle_backend.models.enums.GameStatus;
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

    public String generateReplyForCurrentGuess(String currentGuess,
                                               String wordToGuess,
                                               GameStatus gameStatus,
                                               Integer remainingLives,
                                               Long hintsUsed) {
        String template = promptService.loadPromptTemplate("reply_to_guess.txt");
        return promptService.renderPrompt(template, Map.of(
                "word", wordToGuess,
                "guess", currentGuess,
                "status", gameStatus.toString(),
                "lives", remainingLives.toString(),
                "hintsUsed", hintsUsed.toString()
        ));
    }
}
