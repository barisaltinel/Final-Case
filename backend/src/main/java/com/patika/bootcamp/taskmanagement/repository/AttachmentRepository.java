package com.patika.bootcamp.taskmanagement.repository;

import com.patika.bootcamp.taskmanagement.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
