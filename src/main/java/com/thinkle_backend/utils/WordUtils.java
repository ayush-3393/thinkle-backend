package com.thinkle_backend.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Random;

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

    public static String getRandomLocalWord() {
        Random random = new Random();
        try {
            ClassPathResource resource = new ClassPathResource("prompts/local_words_list.txt");
            try (InputStream inputStream = resource.getInputStream()) {
                String content = new String(inputStream.readAllBytes());
                String[] words = content.split(",");
                return words[random.nextInt(words.length)].trim().toUpperCase();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load local words list for fallback", e);
        }
    }
}
