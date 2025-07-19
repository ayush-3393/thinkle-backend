package com.thinkle_backend.ai.features.replyToGuesses;

import com.thinkle_backend.models.enums.GameStatus;

public interface ReplyToGuessesGenerator {
    String generateReplyToTheGuessedWord(String currentGuess,
                                         String wordToGuess,
                                         GameStatus gameStatus,
                                         Integer remainingLives,
                                         Long hintsUsed);
}
