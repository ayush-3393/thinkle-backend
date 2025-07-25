package com.thinkle_backend.controllers;

import com.thinkle_backend.dtos.requests.CreateHintTypeRequestDto;
import com.thinkle_backend.dtos.requests.DeleteHintTypeRequestDto;
import com.thinkle_backend.dtos.requests.ReActivateHintTypeRequestDto;
import com.thinkle_backend.dtos.requests.UpdateHintTypeRequestDto;
import com.thinkle_backend.dtos.responses.BaseResponse;
import com.thinkle_backend.dtos.responses.HintTypeResponseDto;
import com.thinkle_backend.dtos.responses.UpdateHintTypeResponseDto;
import com.thinkle_backend.models.HintType;
import com.thinkle_backend.services.HintTypeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hint-types")
@CrossOrigin(origins = "http://localhost:3000")
public class HintTypeController {

    private final HintTypeService hintTypeService;

    public HintTypeController(HintTypeService hintTypeService) {
        this.hintTypeService = hintTypeService;
    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponse<HintTypeResponseDto>> createHintType(
            @Valid @RequestBody CreateHintTypeRequestDto createHintTypeRequestDto
            ){
        HintType hintType = this.hintTypeService.createHintType(createHintTypeRequestDto);
        HintTypeResponseDto responseDto = new HintTypeResponseDto();
        responseDto.setType(hintType.getHintType());
        responseDto.setDisplayName(hintType.getDisplayName());

        return new ResponseEntity<BaseResponse<HintTypeResponseDto>>(
                BaseResponse.success(responseDto),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse<HintType>> deleteHintType(
            @Valid @RequestBody DeleteHintTypeRequestDto deleteHintTypeRequestDto
            ){
        HintType deletedHintType = this.hintTypeService.deleteHintType(deleteHintTypeRequestDto.getType());
        return new ResponseEntity<BaseResponse<HintType>>(
                BaseResponse.success("Successfully Deleted", deletedHintType),
                HttpStatus.OK
        );
    }

    @PostMapping("/re-activate")
    public ResponseEntity<BaseResponse<HintType>> reActivateHintType(
            @Valid @RequestBody ReActivateHintTypeRequestDto reActivateHintTypeRequestDto
    ){
        HintType reActivateHintType = this.hintTypeService.reActivateHintType(reActivateHintTypeRequestDto.getType());
        return new ResponseEntity<BaseResponse<HintType>>(
                BaseResponse.success("Successfully Reactivated", reActivateHintType),
                HttpStatus.OK
        );
    }

    @PutMapping("/update")
    public ResponseEntity<BaseResponse<UpdateHintTypeResponseDto>> updateHintType(
            @Valid @RequestBody UpdateHintTypeRequestDto requestDto) {

        UpdateHintTypeResponseDto responseDto = this.hintTypeService.updateHintType(requestDto);
        return ResponseEntity.ok(BaseResponse.success("Successfully Updated", responseDto));
    }
}
