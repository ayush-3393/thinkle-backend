package com.thinkle_backend.models;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Guess extends BaseModel {

    @ManyToOne
    private GameSession gameSession;

    private String guessedWord;

    @ElementCollection
    private List<Integer> correctPositionIndices;

    @ElementCollection
    private List<Integer> missedPositionIndices;

    private LocalDateTime timestamp;
}
