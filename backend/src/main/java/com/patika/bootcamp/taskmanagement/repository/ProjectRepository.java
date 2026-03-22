package com.patika.bootcamp.taskmanagement.repository;

import com.patika.bootcamp.taskmanagement.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByDeletedFalse();

    Optional<Project> findByIdAndDeletedFalse(Long id);
}
