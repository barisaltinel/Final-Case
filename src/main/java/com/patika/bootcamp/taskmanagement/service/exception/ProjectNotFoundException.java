package com.patika.bootcamp.taskmanagement.service.exception;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException() {
        super("Project not found!");
    }
}