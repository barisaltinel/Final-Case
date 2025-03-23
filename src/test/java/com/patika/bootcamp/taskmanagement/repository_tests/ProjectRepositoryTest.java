package com.patika.bootcamp.taskmanagement.repository_tests;

import com.patika.bootcamp.taskmanagement.model.Project;
import com.patika.bootcamp.taskmanagement.model.ProjectStatus;
import com.patika.bootcamp.taskmanagement.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest // Spring Boot'un H2 ile test için veri tabanı oluşturmasını sağlar
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    private Project testProject;

    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setTitle("Test Project");
        testProject.setDescription("Repository Test");
        testProject.setStatus(ProjectStatus.IN_PROGRESS);
        testProject.setDepartmentName("IT");
        projectRepository.save(testProject);
    }

    /** ✅ Tüm projeleri getirme testi */
    @Test
    void shouldReturnAllProjects() {
        List<Project> projects = projectRepository.findAll();
        assertThat(projects).isNotEmpty();
    }

    /** ✅ Proje güncelleme testi */
    @Test
    void shouldUpdateProjectSuccessfully() {
        testProject.setTitle("Updated Title");
        projectRepository.save(testProject);

        Optional<Project> updatedProject = projectRepository.findById(testProject.getId());
        assertThat(updatedProject).isPresent();
        assertThat(updatedProject.get().getTitle()).isEqualTo("Updated Title");
    }

    /** ✅ Proje soft delete testi */
    @Test
    void shouldSoftDeleteProject() {
        testProject.setStatus(ProjectStatus.CANCELLED);
        projectRepository.save(testProject);

        Optional<Project> deletedProject = projectRepository.findById(testProject.getId());
        assertThat(deletedProject).isPresent();
        assertThat(deletedProject.get().getStatus()).isEqualTo(ProjectStatus.CANCELLED);
    }
}
