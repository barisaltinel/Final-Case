package com.patika.bootcamp.taskmanagement.exception_tests;

import com.patika.bootcamp.taskmanagement.exception.GlobalExceptionHandler;
import com.patika.bootcamp.taskmanagement.service.exception.TaskNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void shouldHandleTaskNotFoundException() {
        TaskNotFoundException exception = new TaskNotFoundException();
        ResponseEntity<String> response = exceptionHandler.handleTaskNotFoundException(exception);
    
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Task not found!"); // Ünlemli haliyle test ediyoruz
    }
    
}
