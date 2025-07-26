package com.thinkle_backend.services.implementations;

import com.thinkle_backend.dtos.requests.CreateHintTypeRequestDto;
import com.thinkle_backend.dtos.requests.UpdateHintTypeRequestDto;
import com.thinkle_backend.dtos.responses.UpdateHintTypeResponseDto;
import com.thinkle_backend.exceptions.*;
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

        Optional<HintType> existingOptional = hintTypeRepository.findByHintTypeIgnoreCase(inputType);

        if (existingOptional.isPresent()) {
            HintType existing = existingOptional.get();
            if (Boolean.TRUE.equals(existing.getIsDeleted())) {
                throw new HintTypeAlreadyDeletedException(
                        "Hint type was deleted, reactivate if required: " + inputType
                );
            }
            throw new HintTypeAlreadyExistsException("Hint type already exists: " + inputType);
        }

        HintType newHintType = new HintType();
        newHintType.setHintType(inputType);
        newHintType.setDisplayName(hintTypeRequestDto.getDisplayName());

        return hintTypeRepository.save(newHintType);
    }

    @Override
    public HintType deleteHintType(String hintType) {
        String normalizedType = hintType.toUpperCase();
        Optional<HintType> optional = hintTypeRepository.findByHintTypeIgnoreCase(normalizedType);

        if (optional.isEmpty()) {
            throw new HintTypeDoesNotExistsException("Hint type does not exist: " + normalizedType);
        }

        HintType type = optional.get();

        if (Boolean.TRUE.equals(type.getIsDeleted())) {
            throw new HintTypeAlreadyDeletedException("Hint type already deleted: " + normalizedType);
        }

        type.softDelete();
        type.setUpdatedAt(LocalDateTime.now());

        return hintTypeRepository.save(type);
    }

    @Override
    public HintType reActivateHintType(String hintType) {
        String normalizedType = hintType.toUpperCase();
        Optional<HintType> optional = hintTypeRepository.findByHintTypeIgnoreCase(normalizedType);

        if (optional.isEmpty()) {
            throw new HintTypeDoesNotExistsException("Hint type does not exist: " + normalizedType);
        }

        HintType type = optional.get();

        if (!Boolean.TRUE.equals(type.getIsDeleted())) {
            throw new HintTypeAlreadyActiveException("Hint type is already active: " + normalizedType);
        }

        type.setIsDeleted(false);
        type.setDeletedAt(null);
        type.setUpdatedAt(LocalDateTime.now());

        return hintTypeRepository.save(type);
    }

    @Override
    public UpdateHintTypeResponseDto updateHintType(UpdateHintTypeRequestDto dto) {
        String currentType = dto.getCurrentType().toUpperCase();

        Optional<HintType> optionalCurrent = hintTypeRepository.findByHintTypeIgnoreCase(currentType);

        if (optionalCurrent.isEmpty()) {
            throw new HintTypeDoesNotExistsException("Hint type does not exist: " + currentType);
        }

        HintType current = optionalCurrent.get();

        if (Boolean.TRUE.equals(current.getIsDeleted())) {
            throw new HintTypeAlreadyDeletedException("Hint type is deleted, reactivate if required: " + currentType);
        }

        return applyHintTypeUpdate(dto, current);
    }

    private UpdateHintTypeResponseDto applyHintTypeUpdate(UpdateHintTypeRequestDto dto, HintType current) {
        boolean updated = false;

        UpdateHintTypeResponseDto responseDto = new UpdateHintTypeResponseDto();
        responseDto.setOldHintType(current);

        // Update type if changed
        if (dto.getUpdatedType() != null &&
                !dto.getUpdatedType().equalsIgnoreCase(current.getHintType())) {

            String newType = dto.getUpdatedType().toUpperCase();

            Optional<HintType> existingWithNewType = hintTypeRepository.findByHintTypeIgnoreCase(newType);
            if (existingWithNewType.isPresent()) {
                throw new HintTypeAlreadyExistsException("Hint type already exists: " + newType);
            }

            current.setHintType(newType);
            updated = true;
        }

        // Update display name if changed
        if (dto.getUpdatedDisplayName() != null &&
                !dto.getUpdatedDisplayName().equals(current.getDisplayName())) {
            current.setDisplayName(dto.getUpdatedDisplayName());
            updated = true;
        }

        if (updated) {
            current.setUpdatedAt(LocalDateTime.now());
            hintTypeRepository.save(current);
        }

        responseDto.setUpdatedHintType(current);
        return responseDto;
    }
}
