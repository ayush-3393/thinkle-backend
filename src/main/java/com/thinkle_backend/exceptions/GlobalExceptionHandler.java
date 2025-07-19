package com.thinkle_backend.exceptions;

import com.thinkle_backend.dtos.responses.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HintTypeAlreadyExistsException.class)
    public ResponseEntity<BaseResponse<Void>> handleHintTypeAlreadyExists(HintTypeAlreadyExistsException ex) {
        BaseResponse<Void> response = BaseResponse.failure(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(HintTypeDoesNotExistsException.class)
    public ResponseEntity<BaseResponse<Void>> handleHintTypeDoesNotExists(HintTypeDoesNotExistsException ex) {
        BaseResponse<Void> response = BaseResponse.failure(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HintTypeAlreadyDeletedException.class)
    public ResponseEntity<BaseResponse<Void>> handleHintTypeAlreadyDeleted(HintTypeAlreadyDeletedException ex) {
        BaseResponse<Void> response = BaseResponse.failure(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HintTypeAlreadyActiveException.class)
    public ResponseEntity<BaseResponse<Void>> handleHintTypeAlreadyActive(HintTypeAlreadyActiveException ex) {
        BaseResponse<Void> response = BaseResponse.failure(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGenericException(Exception ex) {
        BaseResponse<Void> response = BaseResponse.failure("Something went wrong: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder("Validation failed: ");
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorMessage.append(fieldError.getField()).append(" ").append(fieldError.getDefaultMessage()).append("; ");
        }
        BaseResponse<Void> response = BaseResponse.failure(errorMessage.toString().trim());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
