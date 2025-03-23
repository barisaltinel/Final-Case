package com.patika.bootcamp.taskmanagement.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patika.bootcamp.taskmanagement.model.Project;
import com.patika.bootcamp.taskmanagement.model.ProjectStatus;
import com.patika.bootcamp.taskmanagement.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import jakarta.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional 

class ProjectIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // JSON dönüşümü için

    @Autowired
    private ProjectRepository projectRepository;

    private Project testProject;

    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setTitle("Test Project");
        testProject.setDescription("Project for testing");
        testProject.setStatus(ProjectStatus.IN_PROGRESS);
        testProject.setDepartmentName("IT");
        projectRepository.save(testProject);
    }

    /** ✅ Tüm projeleri listeleme testi (PROJECT_MANAGER yetkisi ile) */
    @Test
    @WithMockUser(username = "project_manager", roles = "PROJECT_MANAGER")
    void shouldReturnAllProjects() throws Exception {
        mockMvc.perform(get("/api/projects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /** ✅ Yeni proje oluşturma testi (PROJECT_MANAGER yetkisi ile) */
    @Test
    @WithMockUser(username = "project_manager", roles = "PROJECT_MANAGER")
    void shouldCreateProjectSuccessfully() throws Exception {
        Project newProject = new Project();
        newProject.setTitle("New Test Project");
        newProject.setDescription("Integration Test");
        newProject.setStatus(ProjectStatus.IN_PROGRESS);
        newProject.setDepartmentName("HR");

        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProject)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Test Project"));
    }

    /** ✅ Geçersiz proje oluşturma testi (eksik title alanı) */
    @Test
    @WithMockUser(username = "project_manager", roles = "PROJECT_MANAGER")
    void shouldFailToCreateProjectWithoutTitle() throws Exception {
        Project newProject = new Project();
        newProject.setDescription("Integration Test");
        newProject.setStatus(ProjectStatus.IN_PROGRESS);
        newProject.setDepartmentName("HR");

        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProject)))
                .andExpect(status().isBadRequest());
    }

    /** ✅ Proje güncelleme testi (PROJECT_MANAGER yetkisi ile) */
    @Test
    @WithMockUser(username = "project_manager", roles = "PROJECT_MANAGER")
    void shouldUpdateProjectSuccessfully() throws Exception {
        testProject.setTitle("Updated Project Title");

        mockMvc.perform(put("/api/projects/" + testProject.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProject)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Project Title"));
    }

    /** ✅ Proje soft delete testi (ADMIN yetkisi gerektiriyor) */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldSoftDeleteProject() throws Exception {
        mockMvc.perform(delete("/api/projects/" + testProject.getId()))
                .andExpect(status().isNoContent());

        assertThat(projectRepository.findById(testProject.getId())).isPresent();
    }
}
