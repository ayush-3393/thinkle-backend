package com.thinkle_backend.dtos.responses;

import com.thinkle_backend.models.WordHint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class HintsInfoForSession {
    private Integer numberOfHintsUsed;
    private List<HintDetails> hintDetails;
}
