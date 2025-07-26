package com.thinkle_backend.services;

import com.thinkle_backend.dtos.requests.CreateHintTypeRequestDto;
import com.thinkle_backend.dtos.requests.UpdateHintTypeRequestDto;
import com.thinkle_backend.dtos.responses.UpdateHintTypeResponseDto;
import com.thinkle_backend.models.HintType;

public interface HintTypeService {
    HintType createHintType(CreateHintTypeRequestDto createHintTypeRequestDto);
    HintType deleteHintType(String hintType);
    HintType reActivateHintType(String hintType);
    UpdateHintTypeResponseDto updateHintType(UpdateHintTypeRequestDto updateHintTypeRequestDto);
}
