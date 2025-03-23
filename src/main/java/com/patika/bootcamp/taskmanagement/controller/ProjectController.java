package com.patika.bootcamp.taskmanagement.controller;

import com.patika.bootcamp.taskmanagement.model.Project;
import com.patika.bootcamp.taskmanagement.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /** ✅ Projeleri listeleme (Sadece PROJECT_MANAGER ve ADMIN erişebilir) */
    @GetMapping
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'ADMIN')")
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    /** ✅ Yeni proje oluşturma (Sadece PROJECT_MANAGER ve ADMIN) */
    @PostMapping
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'ADMIN')")
    public ResponseEntity<Project> createProject(@Valid @RequestBody Project project) {
        return new ResponseEntity<>(projectService.create(project), HttpStatus.CREATED);
    }

    /** ✅ Proje güncelleme (Sadece PROJECT_MANAGER ve ADMIN) */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'ADMIN')")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @Valid @RequestBody Project updatedProject) {
        return ResponseEntity.ok(projectService.update(id, updatedProject));
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'ADMIN')")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.findById(id));
    }
    /** ✅ Projeyi soft delete yapma (Sadece ADMIN yetkisi olan kullanıcı silebilir) */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> softDeleteProject(@PathVariable Long id) {
        projectService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
