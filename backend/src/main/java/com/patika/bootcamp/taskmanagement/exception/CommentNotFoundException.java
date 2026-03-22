package com.patika.bootcamp.taskmanagement.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException() {
        super("Comment not found!");
    }
}

