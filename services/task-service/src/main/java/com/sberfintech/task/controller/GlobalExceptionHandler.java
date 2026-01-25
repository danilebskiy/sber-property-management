package com.sberfintech.task.controller;

import com.sberfintech.task.domain.exception.TaskNotFoundException;
import com.sberfintech.task.domain.exception.TaskOperationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleTaskNotFound(TaskNotFoundException e) {
        return  Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "Task not found",
                "message", e.getMessage()
        );
    }

    public Map<String, Object> handleTaskOperation(TaskOperationException e) {
        return  Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Invalid Task Operation",
                "message", e.getMessage()
        );
    }
}
