package com.inditex.product.infraestructure.adapter.input.advice;

import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebClientResponseException.NotFound.class)
    public ResponseEntity<@NonNull Void> handleNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<@NonNull Void> handleInternalServerError() {
        return ResponseEntity.internalServerError().build();
    }

}
