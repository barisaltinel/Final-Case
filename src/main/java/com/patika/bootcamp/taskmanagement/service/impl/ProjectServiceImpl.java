package com.patika.bootcamp.taskmanagement.service.impl;

import com.patika.bootcamp.taskmanagement.model.Project;
import com.patika.bootcamp.taskmanagement.repository.ProjectRepository;
import com.patika.bootcamp.taskmanagement.service.ProjectService;
import com.patika.bootcamp.taskmanagement.service.exception.ProjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    public Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException());
    }

    @Override
    public Project create(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public Project update(Long id, Project projectDetails) {
        Project existingProject = findById(id);
        existingProject.setTitle(projectDetails.getTitle());
        existingProject.setDescription(projectDetails.getDescription());
        existingProject.setStatus(projectDetails.getStatus());
        return projectRepository.save(existingProject);
    }

    @Override
    public void softDelete(Long id) {
        Project project = findById(id);
        project.setStatus(null); // Silinen projeler için durumu null yapıyoruz.
        projectRepository.save(project);
    }
}
