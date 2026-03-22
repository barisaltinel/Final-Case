package com.patika.bootcamp.taskmanagement.service.impl;

import com.patika.bootcamp.taskmanagement.model.Task;
import com.patika.bootcamp.taskmanagement.model.TaskState;
import com.patika.bootcamp.taskmanagement.repository.TaskRepository;
import com.patika.bootcamp.taskmanagement.service.TaskService;
import com.patika.bootcamp.taskmanagement.exception.AccessDeniedException;
import com.patika.bootcamp.taskmanagement.exception.TaskCannotBeModifiedException;
import com.patika.bootcamp.taskmanagement.exception.TaskNotFoundException;
import com.patika.bootcamp.taskmanagement.util.SecurityUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> getAllTasks() {
        if (SecurityUtils.hasAnyRole("ADMIN", "PROJECT_MANAGER", "TEAM_LEADER")) {
            return taskRepository.findAll();
        }

        String currentUsername = SecurityUtils.getCurrentUsername();
        if (!StringUtils.hasText(currentUsername)) {
            throw new AccessDeniedException();
        }

        return taskRepository.findAll().stream()
                .filter(this::canAccessAsTeamMember)
                .toList();
    }

    @Override
    public Task findById(@NonNull Long id) {
        Long requiredId = requireId(id, "Task id");

        Task task = taskRepository.findById(requiredId)
                .orElseThrow(TaskNotFoundException::new);
        validateReadAccess(task);
        return task;
    }

    @Override
    public Task create(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task details are required");
        }

        task.setState(TaskState.BACKLOG);
        return taskRepository.save(task);
    }

    @Override
    public Task update(@NonNull Long id, Task taskDetails) {
        if (taskDetails == null) {
            throw new IllegalArgumentException("Task details are required");
        }

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
    public Task cancel(@NonNull Long id, String reason) {
        Task task = findById(id);

        if (task.getState() == TaskState.COMPLETED) {
            throw new TaskCannotBeModifiedException();
        }

        if (!StringUtils.hasText(reason)) {
            throw new IllegalArgumentException("A reason must be provided when cancelling a task");
        }

        task.setState(TaskState.CANCELLED);
        task.setReason(reason.trim());
        return taskRepository.save(task);
    }

    private void validateReadAccess(Task task) {
        if (SecurityUtils.hasAnyRole("ADMIN", "PROJECT_MANAGER", "TEAM_LEADER")) {
            return;
        }

        if (!canAccessAsTeamMember(task)) {
            throw new AccessDeniedException();
        }
    }

    private boolean canAccessAsTeamMember(Task task) {
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (!StringUtils.hasText(currentUsername) || task == null || task.getAssignee() == null) {
            return false;
        }

        String assigneeEmail = task.getAssignee().getEmail();
        if (!StringUtils.hasText(assigneeEmail)) {
            return false;
        }

        return currentUsername.equalsIgnoreCase(assigneeEmail);
    }

    private @NonNull Long requireId(@Nullable Long id, String fieldName) {
        return Objects.requireNonNull(id, fieldName + " is required");
    }
}

