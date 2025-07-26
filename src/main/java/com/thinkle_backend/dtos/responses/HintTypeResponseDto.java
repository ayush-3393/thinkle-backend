package com.thinkle_backend.dtos.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class HintTypeResponseDto {
    private String type;
    private String displayName;
}
