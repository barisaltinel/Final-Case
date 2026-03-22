package com.patika.bootcamp.taskmanagement.service;

import com.patika.bootcamp.taskmanagement.model.Task;
import com.patika.bootcamp.taskmanagement.exception.TaskNotFoundException;
import org.springframework.lang.NonNull;

import java.util.List;

public interface TaskService {
    List<Task> getAllTasks();
    Task findById(@NonNull Long id) throws TaskNotFoundException;
    Task create(Task task);
    Task update(@NonNull Long id, Task task) throws TaskNotFoundException;
    Task cancel(@NonNull Long id, String reason) throws TaskNotFoundException; 
}

