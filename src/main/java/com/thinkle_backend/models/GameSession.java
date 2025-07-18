package com.thinkle_backend.models;

import com.thinkle_backend.models.enums.GameStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class GameSession extends BaseModel {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ThinkleUsers user;

    @Column(name = "game_date", nullable = false)
    private LocalDate gameDate;

    private Integer remainingLives;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Guess> guesses;

    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hint> hints;

    @ManyToOne
    @JoinColumn(name = "word_of_the_day_id", nullable = false)
    private WordOfTheDay wordOfTheDay;
}
