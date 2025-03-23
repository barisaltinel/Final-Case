package com.patika.bootcamp.taskmanagement.service.exception;

public class EmptyFileException extends RuntimeException {
    public EmptyFileException() {
        super("Cannot upload an empty file.");
    }
}