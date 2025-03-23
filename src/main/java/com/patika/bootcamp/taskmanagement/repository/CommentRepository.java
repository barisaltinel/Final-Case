package com.patika.bootcamp.taskmanagement.repository;

import com.patika.bootcamp.taskmanagement.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
