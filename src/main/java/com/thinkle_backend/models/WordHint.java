package com.thinkle_backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "word_hint", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"word_of_the_day_id", "hint_type_id"})
})
@NoArgsConstructor
@Setter
@Getter
public class WordHint extends BaseModel {

    @ManyToOne(optional = false)
    @JoinColumn(name = "word_of_the_day_id")
    private WordOfTheDay wordOfTheDay;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hint_type_id")
    private HintType hintType;

    @Column(columnDefinition = "TEXT")
    private String text;
}

