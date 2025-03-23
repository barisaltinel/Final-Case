package com.patika.bootcamp.taskmanagement.service_tests;

import com.patika.bootcamp.taskmanagement.model.Project;
import com.patika.bootcamp.taskmanagement.model.ProjectStatus;
import com.patika.bootcamp.taskmanagement.repository.ProjectRepository;
import com.patika.bootcamp.taskmanagement.service.exception.ProjectNotFoundException;
import com.patika.bootcamp.taskmanagement.service.impl.ProjectServiceImpl;
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
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project mockProject;

    @BeforeEach
    void setUp() {
        mockProject = new Project();
        mockProject.setId(1L);
        mockProject.setTitle("Test Project");
        mockProject.setDescription("Project for testing");
        mockProject.setStatus(ProjectStatus.IN_PROGRESS);
    }

    @Test
    void shouldReturnAllProjects() {
        when(projectRepository.findAll()).thenReturn(List.of(mockProject));
        List<Project> projects = projectService.getAllProjects();
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0)).isEqualTo(mockProject);
    }

    @Test
    void shouldReturnProjectById() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));
        Project project = projectService.findById(1L);
        assertThat(project).isEqualTo(mockProject);
    }

    @Test
    void shouldThrowExceptionWhenProjectNotFound() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> projectService.findById(99L))
                .isInstanceOf(ProjectNotFoundException.class)
                .hasMessageContaining("Project not found");
    }

    @Test
    void shouldCreateProject() {
        when(projectRepository.save(any(Project.class))).thenReturn(mockProject);
        Project project = projectService.create(mockProject);
        assertThat(project).isEqualTo(mockProject);
    }

    @Test
    void shouldUpdateProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));
        when(projectRepository.save(any(Project.class))).thenReturn(mockProject);
        Project updatedProject = projectService.update(1L, mockProject);
        assertThat(updatedProject).isEqualTo(mockProject);
    }

    @Test
    void shouldSoftDeleteProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));
        projectService.softDelete(1L);
        assertThat(mockProject.getStatus()).isNull();
        verify(projectRepository, times(1)).save(mockProject);
    }
}
