package com.thinkle_backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "hint_type")
public class HintType extends BaseModel {

    @Column(name = "hint_type", unique = true, nullable = false, length = 50)
    private String hintType;

    @Column(name = "display_name", length = 100)
    private String displayName;
}
