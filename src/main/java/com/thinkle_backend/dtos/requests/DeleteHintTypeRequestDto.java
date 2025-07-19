package com.thinkle_backend.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class DeleteHintTypeRequestDto {
    @NotBlank(message = "Type is required")
    @Pattern(regexp = "^[^ ]+$", message = "Type must not contain spaces")
    private String type;
}
