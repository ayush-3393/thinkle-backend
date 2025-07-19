package com.thinkle_backend.dtos.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class HintDetails {
    private String hintType;
    private String hintText;
}
