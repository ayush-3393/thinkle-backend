package com.thinkle_backend.dtos.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class HintsInfoForSession {
    private Integer numberOfHintsUsed;
    private List<HintDetails> usedHintDetails;
}
