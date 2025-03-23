        package com.patika.bootcamp.taskmanagement.integration_tests;

        import com.fasterxml.jackson.databind.ObjectMapper;
        import com.patika.bootcamp.taskmanagement.model.*;
        import com.patika.bootcamp.taskmanagement.repository.*;
        import org.junit.jupiter.api.*;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
        import org.springframework.boot.test.context.SpringBootTest;
        import org.springframework.http.MediaType;
        import org.springframework.security.test.context.support.WithMockUser;
        import org.springframework.test.web.servlet.MockMvc;
        import jakarta.transaction.Transactional;
        import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                task = taskRepository.save(task);
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
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
        }
