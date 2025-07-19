package com.thinkle_backend.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class UpdateHintTypeRequestDto {

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "^[^ ]+$", message = "Current Type must not contain spaces")
    private String currentType;

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "^[^ ]+$", message = "New Type must not contain spaces")
    private String updatedType;

    private String updatedDisplayName;
}
