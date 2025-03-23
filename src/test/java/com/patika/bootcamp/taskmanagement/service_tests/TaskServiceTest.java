package com.patika.bootcamp.taskmanagement.service_tests;

import com.patika.bootcamp.taskmanagement.model.Task;
import com.patika.bootcamp.taskmanagement.model.TaskState;
import com.patika.bootcamp.taskmanagement.repository.TaskRepository;
import com.patika.bootcamp.taskmanagement.service.exception.TaskCannotBeModifiedException;
import com.patika.bootcamp.taskmanagement.service.exception.TaskNotFoundException;
import com.patika.bootcamp.taskmanagement.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        when(taskRepository.save(any(Task.class))).thenReturn(mockTask);
        Task task = taskService.create(mockTask);
        assertThat(task.getState()).isEqualTo(TaskState.BACKLOG);
    }

    @Test
    void shouldUpdateTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(taskRepository.save(any(Task.class))).thenReturn(mockTask);
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
        when(taskRepository.save(any(Task.class))).thenReturn(mockTask);
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
}
