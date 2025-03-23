package com.patika.bootcamp.taskmanagement.service;

import com.patika.bootcamp.taskmanagement.model.Project;
import com.patika.bootcamp.taskmanagement.service.exception.ProjectNotFoundException;

import java.util.List;

public interface ProjectService {
    List<Project> getAllProjects();
    Project create(Project project);
    Project findById(Long id) throws ProjectNotFoundException;
    Project update(Long id, Project projectDetails);
    void softDelete(Long id);
}
