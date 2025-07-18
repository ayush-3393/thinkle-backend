package com.thinkle_backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class WordOfTheDay extends BaseModel {

    private String solutionWord;

    @Column(nullable = false, unique = true)
    private LocalDate generatedAt;

    @PrePersist
    public void prePersist() {
        if (generatedAt == null) {
            generatedAt = LocalDate.now(); // ensures it's always today if not set manually
        }
    }
}
