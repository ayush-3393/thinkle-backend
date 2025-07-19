package com.thinkle_backend.services;

import com.thinkle_backend.models.WordOfTheDay;

public interface WordHintService {
    void createHintsForWordOfTheDay(WordOfTheDay wordOfTheDay);
    String getHintForHintType(String hintType, Long userId);
}
