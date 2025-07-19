package com.thinkle_backend.utils;

public class WordUtils {
    public static boolean isValidWord(String word, Integer maxWordLength) {
        if (word == null || word.isEmpty()) {
            return false;
        }

        if(word.length() > maxWordLength){
            return false;
        }

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (!Character.isLetter(c)) {
                return false;
            }
        }

        return true;
    }
}
