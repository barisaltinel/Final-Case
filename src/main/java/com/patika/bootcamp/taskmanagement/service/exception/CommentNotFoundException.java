package com.patika.bootcamp.taskmanagement.service.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException() {
        super("Comment not found!");
    }
}
