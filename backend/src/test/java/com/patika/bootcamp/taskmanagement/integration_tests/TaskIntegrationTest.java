package com.patika.bootcamp.taskmanagement.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patika.bootcamp.taskmanagement.model.Project;
import com.patika.bootcamp.taskmanagement.model.ProjectStatus;
import com.patika.bootcamp.taskmanagement.model.Task;
import com.patika.bootcamp.taskmanagement.model.TaskPriority;
import com.patika.bootcamp.taskmanagement.model.TaskState;
import com.patika.bootcamp.taskmanagement.repository.ProjectRepository;
import com.patika.bootcamp.taskmanagement.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private Task task;
    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setTitle("Test Project");
        project.setDescription("Project for testing");
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setDepartmentName("IT");
        project = projectRepository.save(project);

        task = new Task();
        task.setTitle("Test Task");
        task.setDescription("This is a test task");
        task.setState(TaskState.BACKLOG);
        task.setPriority(TaskPriority.MEDIUM);
        task.setProject(project);
        task = requireTask(taskRepository.save(task), "Saved task is required");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldCreateTaskSuccessfully() throws Exception {
        Task newTask = new Task();
        newTask.setTitle("New Task");
        newTask.setDescription("Integration Test");
        newTask.setState(TaskState.IN_PROGRESS);
        newTask.setPriority(TaskPriority.HIGH);
        newTask.setProject(project);

        mockMvc.perform(post("/api/tasks")
                        .contentType(requireMediaType(MediaType.APPLICATION_JSON))
                        .content(requireContent(objectMapper.writeValueAsString(newTask))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Task"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldCancelTaskSuccessfully() throws Exception {
        mockMvc.perform(put("/api/tasks/" + task.getId() + "/cancel")
                        .param("reason", "No longer needed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("CANCELLED"))
                .andExpect(jsonPath("$.reason").value("No longer needed"));
    }

    private @NonNull Task requireTask(@Nullable Task task, String message) {
        return Objects.requireNonNull(task, message);
    }

    private @NonNull MediaType requireMediaType(@Nullable MediaType mediaType) {
        return Objects.requireNonNull(mediaType, "MediaType is required");
    }

    private @NonNull String requireContent(@Nullable String content) {
        return Objects.requireNonNull(content, "Serialized content is required");
    }
}
