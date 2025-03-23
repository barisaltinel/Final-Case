package com.patika.bootcamp.taskmanagement.service.impl;

import com.patika.bootcamp.taskmanagement.model.Task;
import com.patika.bootcamp.taskmanagement.model.TaskState;
import com.patika.bootcamp.taskmanagement.repository.TaskRepository;
import com.patika.bootcamp.taskmanagement.service.TaskService;
import com.patika.bootcamp.taskmanagement.service.exception.TaskCannotBeModifiedException;
import com.patika.bootcamp.taskmanagement.service.exception.TaskNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException());
    }

    @Override
    public Task create(Task task) {
        task.setState(TaskState.BACKLOG); // Yeni görevler varsayılan olarak BACKLOG durumunda başlar.
        return taskRepository.save(task);
    }

    @Override
    public Task update(Long id, Task taskDetails) {
        Task existingTask = findById(id);

        if (existingTask.getState() == TaskState.COMPLETED) {
            throw new TaskCannotBeModifiedException();
        }

        existingTask.setTitle(taskDetails.getTitle());
        existingTask.setDescription(taskDetails.getDescription());
        existingTask.setPriority(taskDetails.getPriority());
        existingTask.setState(taskDetails.getState());

        return taskRepository.save(existingTask);
    }

    @Override
    public Task cancel(Long id, String reason) {
        Task task = findById(id);

        if (task.getState() == TaskState.COMPLETED) {
            throw new TaskCannotBeModifiedException();
        }

        if (reason == null || reason.isEmpty()) {
            throw new IllegalArgumentException("A reason must be provided when cancelling a task");
        }

        task.setState(TaskState.CANCELLED);
        task.setReason(reason);
        return taskRepository.save(task);
    }
}
