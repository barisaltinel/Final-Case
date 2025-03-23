package com.patika.bootcamp.taskmanagement.service.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("You do not have permission to perform this action!");
    }

    public AccessDeniedException(String message) {
        super(message);
    }
}
