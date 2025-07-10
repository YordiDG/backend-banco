package com.banco.bancodemo.exceptions;

import com.banco.bancodemo.dto.ApiResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CuentaNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleCuentaNotFound(CuentaNotFoundException ex) {
        log.error("Cuenta no encontrada: {}", ex.getMessage());

        ApiResponseDTO<Object> response = ApiResponseDTO.builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleSaldoInsuficiente(SaldoInsuficienteException ex) {
        log.error("Saldo insuficiente: {}", ex.getMessage());

        ApiResponseDTO<Object> response = ApiResponseDTO.builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(OperacionNoPermitidaException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleOperacionNoPermitida(OperacionNoPermitidaException ex) {
        log.error("Operación no permitida: {}", ex.getMessage());

        ApiResponseDTO<Object> response = ApiResponseDTO.builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(TransaccionNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleTransaccionNotFound(TransaccionNotFoundException ex) {
        log.error("Transacción no encontrada: {}", ex.getMessage());

        ApiResponseDTO<Object> response = ApiResponseDTO.builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(TitularCuentaNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleTitularCuentaNotFound(TitularCuentaNotFoundException ex) {
        log.error("Titular de cuenta no encontrado: {}", ex.getMessage());

        ApiResponseDTO<Object> response = ApiResponseDTO.builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleValidation(ValidationException ex) {
        log.error("Error de validación: {}", ex.getMessage());

        ApiResponseDTO<Object> response = ApiResponseDTO.builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.error("Errores de validación en los campos: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponseDTO<Object> response = ApiResponseDTO.builder()
                .success(false)
                .message("Errores de validación en los campos enviados")
                .data(errors)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleGenericException(Exception ex) {
        log.error("Error interno del servidor: {}", ex.getMessage(), ex);

        ApiResponseDTO<Object> response = ApiResponseDTO.builder()
                .success(false)
                .message("Error interno del servidor")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
