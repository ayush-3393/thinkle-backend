package com.thinkle_backend.exceptions;

import com.thinkle_backend.ai.exceptions.AiMessageParserException;
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

    @ExceptionHandler(AiMessageParserException.class)
    public ResponseEntity<BaseResponse<Void>> handleAiMessageParser(AiMessageParserException ex) {
        BaseResponse<Void> response = BaseResponse.failure(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WordAlreadyExistsException.class)
    public ResponseEntity<BaseResponse<Void>> handleWordAlreadyExists(WordAlreadyExistsException ex) {
        BaseResponse<Void> response = BaseResponse.failure(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidWordException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidWord(InvalidWordException ex) {
        BaseResponse<Void> response = BaseResponse.failure(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(WordDoesNotExistsException.class)
    public ResponseEntity<BaseResponse<Void>> handleWordDoesNotExist(WordDoesNotExistsException ex) {
        BaseResponse<Void> response = BaseResponse.failure(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HintAlreadyExists.class)
    public ResponseEntity<BaseResponse<Void>> handleHintAlreadyExists(HintAlreadyExists ex) {
        BaseResponse<Void> response = BaseResponse.failure(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(GameSessionNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleGameSessionNotFound(GameSessionNotFoundException ex) {
        BaseResponse<Void> response = BaseResponse.failure(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CanNotUseHintException.class)
    public ResponseEntity<BaseResponse<Void>> handleCanNotUseHint(CanNotUseHintException ex) {
        BaseResponse<Void> response = BaseResponse.failure(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HintDoesNotExistsException.class)
    public ResponseEntity<BaseResponse<Void>> handleHintDoesNotExist(HintDoesNotExistsException ex) {
        BaseResponse<Void> response = BaseResponse.failure(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleUserNotFound(UserNotFoundException ex) {
        BaseResponse<Void> response = BaseResponse.failure(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
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
