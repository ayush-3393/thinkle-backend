package com.thinkle_backend.services;

import com.thinkle_backend.dtos.responses.GetHintResponseDto;
import com.thinkle_backend.models.WordOfTheDay;

public interface WordHintService {
    void createHintsForWordOfTheDay(WordOfTheDay wordOfTheDay);
    GetHintResponseDto getHintForHintType(String hintType, Long userId);
}
