package com.patika.bootcamp.taskmanagement.controller;

import com.patika.bootcamp.taskmanagement.model.Attachment;
import com.patika.bootcamp.taskmanagement.service.AttachmentService;
import com.patika.bootcamp.taskmanagement.service.exception.AttachmentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {
    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'TEAM_LEADER', 'PROJECT_MANAGER', 'ADMIN')")
    public ResponseEntity<List<Attachment>> getAllAttachments() {
        return ResponseEntity.ok(attachmentService.getAllAttachments());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'TEAM_LEADER', 'PROJECT_MANAGER', 'ADMIN')")
    public ResponseEntity<Attachment> getAttachmentById(@PathVariable Long id) {
        return ResponseEntity.ok(attachmentService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'TEAM_LEADER', 'PROJECT_MANAGER', 'ADMIN')")
    public ResponseEntity<Attachment> uploadAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("taskId") Long taskId) {
        return new ResponseEntity<>(attachmentService.upload(file, taskId), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'TEAM_LEADER', 'PROJECT_MANAGER', 'ADMIN')")
    public ResponseEntity<Void> softDeleteAttachment(@PathVariable Long id) {
        attachmentService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(AttachmentNotFoundException.class)
    public ResponseEntity<String> handleAttachmentNotFound(AttachmentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
