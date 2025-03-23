package com.patika.bootcamp.taskmanagement.controller_tests;

import com.patika.bootcamp.taskmanagement.controller.TaskController;
import com.patika.bootcamp.taskmanagement.model.Task;
import com.patika.bootcamp.taskmanagement.model.TaskState;
import com.patika.bootcamp.taskmanagement.service.TaskService;
import com.patika.bootcamp.taskmanagement.service.exception.TaskNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

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
    @WithMockUser
    void shouldReturnAllTasks() {
        when(taskService.getAllTasks()).thenReturn(List.of(mockTask));
        ResponseEntity<List<Task>> response = taskController.getAllTasks();
        assertThat(response.getBody()).isNotNull().hasSize(1);
        assertThat(response.getBody().get(0)).isEqualTo(mockTask);
    }

    @Test
    @WithMockUser
    void shouldReturnTaskById() {
        when(taskService.findById(1L)).thenReturn(mockTask);
        ResponseEntity<Task> response = taskController.getTaskById(1L);
        assertThat(response.getBody()).isNotNull().isEqualTo(mockTask);
    }

    @Test
    @WithMockUser
    void shouldThrowExceptionWhenTaskNotFound() {
        when(taskService.findById(99L)).thenThrow(new TaskNotFoundException());
        assertThatThrownBy(() -> taskController.getTaskById(99L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task not found");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldCreateTask() {
        when(taskService.create(any(Task.class))).thenReturn(mockTask);
        ResponseEntity<Task> response = taskController.createTask(mockTask);
        assertThat(response.getBody()).isNotNull().isEqualTo(mockTask);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldUpdateTask() {
        when(taskService.update(anyLong(), any(Task.class))).thenReturn(mockTask);
        ResponseEntity<Task> response = taskController.updateTask(1L, mockTask);
        assertThat(response.getBody()).isNotNull().isEqualTo(mockTask);
    }

    @Test
    @WithMockUser(username = "project_manager", roles = "PROJECT_MANAGER")
    void shouldCancelTask() {
        when(taskService.cancel(anyLong(), anyString())).thenReturn(mockTask);
        ResponseEntity<Task> response = taskController.cancelTask(1L, "Task no longer needed");
        assertThat(response.getBody()).isNotNull().isEqualTo(mockTask);
    }
}
