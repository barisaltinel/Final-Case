package com.patika.bootcamp.taskmanagement.exception;

public class AttachmentNotFoundException extends RuntimeException {
    public AttachmentNotFoundException() {
        super("Attachment not found!");
    }
}

