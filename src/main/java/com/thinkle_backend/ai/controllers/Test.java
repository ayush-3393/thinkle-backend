package com.thinkle_backend.ai.controllers;

import com.thinkle_backend.models.WordOfTheDay;
import com.thinkle_backend.services.WordOfTheDayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class Test {

    private final WordOfTheDayService wordOfTheDayService;

    public Test(WordOfTheDayService wordOfTheDayService) {
        this.wordOfTheDayService = wordOfTheDayService;
    }

    @PostMapping("/word")
    public ResponseEntity<?> word(){

        WordOfTheDay wordOfTheDay = this.wordOfTheDayService.generateWordOfTheDay();

        return ResponseEntity.ok(wordOfTheDay);
    }
}
