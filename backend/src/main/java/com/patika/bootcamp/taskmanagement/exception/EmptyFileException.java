package com.patika.bootcamp.taskmanagement.exception;

public class EmptyFileException extends RuntimeException {
    public EmptyFileException() {
        super("Cannot upload an empty file.");
    }
}
