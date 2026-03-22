package com.patika.bootcamp.taskmanagement.service.impl;

import com.patika.bootcamp.taskmanagement.model.Project;
import com.patika.bootcamp.taskmanagement.repository.ProjectRepository;
import com.patika.bootcamp.taskmanagement.service.ProjectService;
import com.patika.bootcamp.taskmanagement.exception.ProjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAllByDeletedFalse();
    }

    @Override
    public Project findById(Long id) {
        Long requiredId = requireId(id, "Project id");

        return projectRepository.findByIdAndDeletedFalse(requiredId)
                .orElseThrow(ProjectNotFoundException::new);
    }

    @Override
    public Project create(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Project details are required");
        }

        project.setDeleted(false);
        return projectRepository.save(project);
    }

    @Override
    public Project update(Long id, Project projectDetails) {
        if (projectDetails == null) {
            throw new IllegalArgumentException("Project details are required");
        }

        Project existingProject = findById(id);
        existingProject.setTitle(projectDetails.getTitle());
        existingProject.setDescription(projectDetails.getDescription());
        existingProject.setStatus(projectDetails.getStatus());
        existingProject.setDepartmentName(projectDetails.getDepartmentName());
        return projectRepository.save(existingProject);
    }

    @Override
    public void softDelete(Long id) {
        Project project = findById(id);
        project.setDeleted(true);
        projectRepository.save(project);
    }

    private Long requireId(Long id, String fieldName) {
        return Objects.requireNonNull(id, fieldName + " is required");
    }
}

