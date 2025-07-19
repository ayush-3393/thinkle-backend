package com.thinkle_backend.services.implementations;

import com.thinkle_backend.dtos.requests.CreateHintTypeRequestDto;
import com.thinkle_backend.dtos.requests.UpdateHintTypeRequestDto;
import com.thinkle_backend.dtos.responses.UpdateHintTypeResponseDto;
import com.thinkle_backend.exceptions.HintTypeAlreadyDeletedException;
import com.thinkle_backend.exceptions.HintTypeAlreadyExistsException;
import com.thinkle_backend.exceptions.HintTypeDoesNotExistsException;
import com.thinkle_backend.models.HintType;
import com.thinkle_backend.repositories.HintTypeRepository;
import com.thinkle_backend.services.HintTypeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class HintTypeServiceImpl implements HintTypeService {

    private final HintTypeRepository hintTypeRepository;

    public HintTypeServiceImpl(HintTypeRepository hintTypeRepository) {
        this.hintTypeRepository = hintTypeRepository;
    }

    @Override
    public HintType createHintType(CreateHintTypeRequestDto hintTypeRequestDto) {
        String inputType = hintTypeRequestDto.getType().toUpperCase();

        // Try to fetch existing HintType, regardless of soft delete status
        Optional<HintType> existingHintTypeOptional = this.hintTypeRepository.findByHintTypeIgnoreCase(inputType);

        if (existingHintTypeOptional.isPresent()) {
            HintType existingHintType = existingHintTypeOptional.get();

            if (existingHintType.getIsDeleted()) {
                throw new HintTypeAlreadyDeletedException(
                        "Hint type was deleted, reactivate if required: " + inputType
                );
            }
            throw new HintTypeAlreadyExistsException("Hint type already exists: " + inputType);
        }

        // Create new if not found
        HintType hintType = new HintType();
        hintType.setHintType(inputType);
        hintType.setDisplayName(hintTypeRequestDto.getDisplayName());

        return this.hintTypeRepository.save(hintType);
    }


    @Override
    public HintType deleteHintType(String hintType) {
        Optional<HintType> optionalHintType = this.hintTypeRepository.findByHintTypeIgnoreCase(hintType);

        if(optionalHintType.isEmpty()){
            throw new HintTypeDoesNotExistsException("Hint type does not exist: " + hintType);
        }

        HintType type = optionalHintType.get();

        if(type.getIsDeleted()){
            throw new HintTypeAlreadyDeletedException("Hint Type was already deleted: " + hintType);
        }

        type.softDelete();

        return this.hintTypeRepository.save(type);
    }

    @Override
    public HintType reActivateHintType(String hintType) {
        Optional<HintType> optionalHintType = hintTypeRepository.findByHintTypeIgnoreCase(hintType);
        if (optionalHintType.isEmpty()) {
            throw new HintTypeDoesNotExistsException("Hint type does not exist: " + hintType);
        }

        HintType existingHintType = optionalHintType.get();

        if(!existingHintType.getIsDeleted()){
            throw new HintTypeAlreadyDeletedException("Hint Type is already active: " + hintType);
        }

        existingHintType.setIsDeleted(false);
        existingHintType.setDeletedAt(null);
        existingHintType.setUpdatedAt(LocalDateTime.now());

        return this.hintTypeRepository.save(existingHintType);
    }

    @Override
    public UpdateHintTypeResponseDto updateHintType(UpdateHintTypeRequestDto updateHintTypeRequestDto) {
        Optional<HintType> optionalCurrentHintType =
                hintTypeRepository.findByHintTypeIgnoreCase(updateHintTypeRequestDto.getCurrentType());

        if (optionalCurrentHintType.isEmpty()) {
            throw new HintTypeDoesNotExistsException(
                    "Hint type does not exist: " + updateHintTypeRequestDto.getCurrentType()
            );
        }

        HintType currentHintType = optionalCurrentHintType.get();

        if(currentHintType.getIsDeleted()){
            throw new HintTypeAlreadyDeletedException(
                    "Hint Type was deleted, reactivate if required: " + currentHintType.getHintType()
            );
        }

        return getUpdateHintTypeResponseDto(updateHintTypeRequestDto, currentHintType);
    }

    private UpdateHintTypeResponseDto getUpdateHintTypeResponseDto(
            UpdateHintTypeRequestDto updateHintTypeRequestDto,
            HintType currentHintType
    ) {

        UpdateHintTypeResponseDto responseDto = new UpdateHintTypeResponseDto();
        responseDto.setOldHintType(currentHintType);

        // Handle type update
        if (updateHintTypeRequestDto.getUpdatedType() != null &&
                !updateHintTypeRequestDto.getUpdatedType().equalsIgnoreCase(currentHintType.getHintType())) {

            String newType = updateHintTypeRequestDto.getUpdatedType().toUpperCase();

            Optional<HintType> other = hintTypeRepository.findByHintTypeIgnoreCase(newType);
            if (other.isPresent()) {
                throw new HintTypeAlreadyExistsException("Hint type already exists: " + newType);
            }

            currentHintType.setHintType(newType);
        }

        // Handle display name update
        if (updateHintTypeRequestDto.getUpdatedDisplayName() != null &&
                !updateHintTypeRequestDto.getUpdatedDisplayName().equals(currentHintType.getDisplayName())) {
            currentHintType.setDisplayName(updateHintTypeRequestDto.getUpdatedDisplayName());
        }

        currentHintType.setUpdatedAt(LocalDateTime.now());

        this.hintTypeRepository.save(currentHintType);

        responseDto.setUpdatedHintType(currentHintType);

        return responseDto;
    }
}
