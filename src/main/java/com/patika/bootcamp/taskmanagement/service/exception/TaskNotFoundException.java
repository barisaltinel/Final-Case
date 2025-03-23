package com.patika.bootcamp.taskmanagement.service.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException() {
        super("Task not found!");
    }
}
