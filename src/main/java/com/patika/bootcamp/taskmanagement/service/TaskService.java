package com.patika.bootcamp.taskmanagement.service;

import com.patika.bootcamp.taskmanagement.model.Task;
import com.patika.bootcamp.taskmanagement.service.exception.TaskNotFoundException;
import java.util.List;

public interface TaskService {
    List<Task> getAllTasks();
    Task findById(Long id) throws TaskNotFoundException;
    Task create(Task task);
    Task update(Long id, Task task) throws TaskNotFoundException;
    Task cancel(Long id, String reason) throws TaskNotFoundException; 
}
