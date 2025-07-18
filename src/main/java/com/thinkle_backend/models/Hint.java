package com.thinkle_backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
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

    @ManyToOne
    private GameSession gameSession;

    @ManyToOne
    @JoinColumn(name = "hint_type_id", nullable = false)
    private HintType hintType;

    @Lob
    private String text;

    private LocalDateTime usedAt;
}
