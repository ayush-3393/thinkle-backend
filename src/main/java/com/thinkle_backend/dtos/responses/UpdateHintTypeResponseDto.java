package com.thinkle_backend.dtos.responses;

import com.thinkle_backend.models.HintType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class UpdateHintTypeResponseDto {
    private HintType oldHintType;
    private HintType updatedHintType;
}
