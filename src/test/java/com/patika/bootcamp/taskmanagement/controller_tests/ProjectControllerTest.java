package com.patika.bootcamp.taskmanagement.controller_tests;

import com.patika.bootcamp.taskmanagement.controller.ProjectController;
import com.patika.bootcamp.taskmanagement.model.Project;
import com.patika.bootcamp.taskmanagement.service.ProjectService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private Project mockProject;

    @BeforeEach
    void setUp() {
        mockProject = new Project();
        mockProject.setId(1L);
        mockProject.setTitle("Test Project");
        mockProject.setDescription("Project for testing");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturnAllProjects() {
        when(projectService.getAllProjects()).thenReturn(List.of(mockProject));
        ResponseEntity<List<Project>> response = projectController.getAllProjects();
        assertThat(response.getBody()).isNotNull().hasSize(1);
        assertThat(response.getBody().get(0)).isEqualTo(mockProject);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturnProjectById() {
        when(projectService.findById(1L)).thenReturn(mockProject);
        ResponseEntity<Project> response = projectController.getProjectById(1L);
        assertThat(response.getBody()).isNotNull().isEqualTo(mockProject);
    }

    @Test
    @WithMockUser(username = "project_manager", roles = "PROJECT_MANAGER")
    void shouldCreateProject() {
        when(projectService.create(any(Project.class))).thenReturn(mockProject);
        ResponseEntity<Project> response = projectController.createProject(mockProject);
        assertThat(response.getBody()).isNotNull().isEqualTo(mockProject);
    }

    @Test
    @WithMockUser(username = "project_manager", roles = "PROJECT_MANAGER")
    void shouldUpdateProject() {
        when(projectService.update(anyLong(), any(Project.class))).thenReturn(mockProject);
        ResponseEntity<Project> response = projectController.updateProject(1L, mockProject);
        assertThat(response.getBody()).isNotNull().isEqualTo(mockProject);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldSoftDeleteProject() {
        doNothing().when(projectService).softDelete(1L);
        ResponseEntity<Void> response = projectController.softDeleteProject(1L);
        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }
}
