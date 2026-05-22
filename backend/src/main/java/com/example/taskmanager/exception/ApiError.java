package com.example.taskmanager.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ApiError {
    public LocalDateTime timestamp;
    public int status;
    public String error;
    public String message;
    public List<?> errors;

    public ApiError(int status, String error, String message, List<?> errors) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.errors = errors;
    }
}