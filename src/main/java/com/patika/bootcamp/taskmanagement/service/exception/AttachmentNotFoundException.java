package com.patika.bootcamp.taskmanagement.service.exception;

public class AttachmentNotFoundException extends RuntimeException {
    public AttachmentNotFoundException() {
        super("Attachment not found!");
    }
}
