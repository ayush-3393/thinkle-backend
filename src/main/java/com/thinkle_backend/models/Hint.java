package com.thinkle_backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Hint extends BaseModel {

    @ManyToOne(optional = false)
    private GameSession gameSession;

    @ManyToOne(optional = false)
    private WordHint wordHint;

    private LocalDateTime usedAt;
}
