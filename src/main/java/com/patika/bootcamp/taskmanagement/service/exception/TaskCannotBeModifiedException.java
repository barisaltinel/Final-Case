package com.patika.bootcamp.taskmanagement.service.exception;

public class TaskCannotBeModifiedException extends RuntimeException {
    public TaskCannotBeModifiedException() {
        super("Completed tasks cannot be modified!");
    }
}
