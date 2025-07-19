package com.thinkle_backend.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class GuessUtils {
    private String solutionWord;
    private String guessedWord;
    private List<Integer> correctPositionsOfGuessedWord = new ArrayList<>();
    private List<Integer> missedPositionsOfGuessedWord = new ArrayList<>();

    public GuessUtils(String solutionWord, String guessedWord) {
        this.solutionWord = solutionWord.toUpperCase();
        this.guessedWord = guessedWord.toUpperCase();
        findPositions();
    }

    private void findPositions(){
        boolean[] matched = new boolean[this.solutionWord.length()];
        int[] actualFreq = new int[26];
        boolean[] isCorrect = new boolean[this.guessedWord.length()];

        // Count frequency of letters in actual word
        for (int i = 0; i < this.solutionWord.length(); i++) {
            actualFreq[this.solutionWord.charAt(i) - 'A']++;
        }

        for (int i = 0; i < this.guessedWord.length(); i++) {
            if (this.guessedWord.charAt(i) == this.solutionWord.charAt(i)) {
                this.correctPositionsOfGuessedWord.add(i);
                isCorrect[i] = true;
                actualFreq[this.guessedWord.charAt(i) - 'A']--;
            }
        }

        // Second pass: misplaced letters (yellow)
        for (int i = 0; i < this.guessedWord.length(); i++) {
            if (!isCorrect[i]) {
                char c = this.guessedWord.charAt(i);
                int idx = c - 'A';

                if (actualFreq[idx] > 0) {
                    this.missedPositionsOfGuessedWord.add(i);
                    actualFreq[idx]--;
                }
            }
        }
    }
}
