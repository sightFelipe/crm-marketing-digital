package com.crmmarketingdigitalback2024.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException.*;
import java.time.LocalDateTime;


@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleException(Exception exception) {
        HttpStatus status = determineHttpStatus(exception);
        CustomErrorResponse errorResponse = CustomErrorResponse.builder()
                .description(exception.getLocalizedMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");
    }

    private HttpStatus determineHttpStatus(Exception exception) {
        if (exception instanceof Forbidden) {
            return HttpStatus.FORBIDDEN;
        } else if (exception instanceof NotFound) {
            return HttpStatus.NOT_FOUND;
        } else {
            // Manejo de otras excepciones no específicas aquí
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
