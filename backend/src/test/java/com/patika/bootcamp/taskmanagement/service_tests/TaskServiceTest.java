package com.patika.bootcamp.taskmanagement.service_tests;

import com.patika.bootcamp.taskmanagement.model.Task;
import com.patika.bootcamp.taskmanagement.model.TaskState;
import com.patika.bootcamp.taskmanagement.repository.TaskRepository;
import com.patika.bootcamp.taskmanagement.exception.TaskCannotBeModifiedException;
import com.patika.bootcamp.taskmanagement.exception.TaskNotFoundException;
import com.patika.bootcamp.taskmanagement.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task mockTask;

    @BeforeEach
    void setUp() {
        mockTask = new Task();
        mockTask.setId(1L);
        mockTask.setTitle("Test Task");
        mockTask.setDescription("This is a test task");
        mockTask.setState(TaskState.BACKLOG);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "admin@example.com",
                        "N/A",
                        Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(mockTask));
        List<Task> tasks = taskService.getAllTasks();
        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0)).isEqualTo(mockTask);
    }

    @Test
    void shouldReturnTaskById() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        Task task = taskService.findById(1L);
        assertThat(task).isEqualTo(mockTask);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> taskService.findById(99L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task not found");
    }

    @Test
    void shouldCreateTaskWithDefaultState() {
        when(taskRepository.save(anyTask())).thenReturn(requireTask(mockTask, "mockTask must not be null"));
        Task task = taskService.create(mockTask);
        assertThat(task.getState()).isEqualTo(TaskState.BACKLOG);
    }

    @Test
    void shouldUpdateTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(taskRepository.save(anyTask())).thenReturn(requireTask(mockTask, "mockTask must not be null"));
        Task updatedTask = taskService.update(1L, mockTask);
        assertThat(updatedTask).isEqualTo(mockTask);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingCompletedTask() {
        mockTask.setState(TaskState.COMPLETED);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        assertThatThrownBy(() -> taskService.update(1L, mockTask))
                .isInstanceOf(TaskCannotBeModifiedException.class)
                .hasMessageContaining("Completed tasks cannot be modified");
    }

    @Test
    void shouldCancelTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(taskRepository.save(anyTask())).thenReturn(requireTask(mockTask, "mockTask must not be null"));
        Task canceledTask = taskService.cancel(1L, "No longer needed");
        assertThat(canceledTask.getState()).isEqualTo(TaskState.CANCELLED);
        assertThat(canceledTask.getReason()).isEqualTo("No longer needed");
    }

    @Test
    void shouldThrowExceptionWhenCancellingCompletedTask() {
        mockTask.setState(TaskState.COMPLETED);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        assertThatThrownBy(() -> taskService.cancel(1L, "Task is obsolete"))
                .isInstanceOf(TaskCannotBeModifiedException.class)
                .hasMessageContaining("Completed tasks cannot be modified!");
    }

    @Test
    void shouldThrowExceptionWhenCancellingWithoutReason() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        assertThatThrownBy(() -> taskService.cancel(1L, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A reason must be provided when cancelling a task");
    }

    private @NonNull Task requireTask(@Nullable Task task, String message) {
        return Objects.requireNonNull(task, message);
    }

    private @NonNull Task anyTask() {
        return any(Task.class);
    }
}

