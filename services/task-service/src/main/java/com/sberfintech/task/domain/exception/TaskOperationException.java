package com.sberfintech.task.domain.exception;

public class TaskOperationException extends RuntimeException{
    public TaskOperationException(String message) {
        super(message);
    }
}
