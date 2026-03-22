package com.patika.bootcamp.taskmanagement.service.impl;

import com.patika.bootcamp.taskmanagement.model.Attachment;
import com.patika.bootcamp.taskmanagement.model.Task;
import com.patika.bootcamp.taskmanagement.repository.AttachmentRepository;
import com.patika.bootcamp.taskmanagement.repository.TaskRepository;
import com.patika.bootcamp.taskmanagement.service.AttachmentService;
import com.patika.bootcamp.taskmanagement.exception.AccessDeniedException;
import com.patika.bootcamp.taskmanagement.exception.AttachmentNotFoundException;
import com.patika.bootcamp.taskmanagement.exception.EmptyFileException;
import com.patika.bootcamp.taskmanagement.exception.TaskNotFoundException;
import com.patika.bootcamp.taskmanagement.util.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
public class AttachmentServiceImpl implements AttachmentService {
    private static final String UPLOAD_DIR = "uploads/";
    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of("application/pdf", "text/plain", "image/png", "image/jpeg");

    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository, TaskRepository taskRepository) {
        this.attachmentRepository = attachmentRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Attachment> getAllAttachments() {
        return attachmentRepository.findAll().stream()
                .filter(attachment -> !attachment.isDeleted())
                .filter(this::canAccessAttachment)
                .toList();
    }

    @Override
    public Attachment findById(Long id) {
        Long requiredId = requireId(id, "Attachment id");

        Attachment attachment = attachmentRepository.findById(requiredId)
                .filter(existing -> !existing.isDeleted())
                .orElseThrow(AttachmentNotFoundException::new);

        if (!canAccessAttachment(attachment)) {
            throw new AccessDeniedException();
        }

        return attachment;
    }

    @Override
    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'TEAM_LEADER', 'PROJECT_MANAGER', 'ADMIN')")
    public Attachment upload(MultipartFile file, Long taskId) {
        if (file == null) {
            throw new IllegalArgumentException("File is required");
        }
        Long requiredTaskId = requireId(taskId, "Task id");

        Task task = taskRepository.findById(requiredTaskId)
                .orElseThrow(TaskNotFoundException::new);
        validateTaskAccess(task);

        if (file.isEmpty()) {
            throw new EmptyFileException();
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("File size must be <= 5 MB");
        }
        String mimeType = file.getContentType();
        if (!StringUtils.hasText(mimeType) || !ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new IllegalArgumentException("Unsupported file type");
        }

        String originalFileName = sanitizeFileName(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + "_" + originalFileName;
        String filePath = UPLOAD_DIR + fileName;

        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            throw new IllegalStateException("File upload failed");
        }

        Attachment attachment = new Attachment();
        attachment.setFileName(originalFileName);
        attachment.setFilePath(filePath);
        attachment.setMimeType(mimeType);
        attachment.setFileSize(file.getSize());
        attachment.setTask(task);
        attachment.setUploadedAt(LocalDateTime.now());
        attachment.setDeleted(false);

        return attachmentRepository.save(attachment);
    }

    @Override
    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'TEAM_LEADER', 'PROJECT_MANAGER', 'ADMIN')")
    public Attachment update(Long id, Attachment updatedAttachment) {
        if (updatedAttachment == null) {
            throw new IllegalArgumentException("Attachment details are required");
        }

        Attachment existingAttachment = findById(id);
        existingAttachment.setFileName(sanitizeFileName(updatedAttachment.getFileName()));
        return attachmentRepository.save(existingAttachment);
    }

    @Override
    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'TEAM_LEADER', 'PROJECT_MANAGER', 'ADMIN')")
    public void softDelete(Long id) {
        Attachment attachment = findById(id);
        attachment.markAsDeleted();
        attachmentRepository.save(attachment);
    }

    private boolean canAccessAttachment(Attachment attachment) {
        if (SecurityUtils.hasAnyRole("ADMIN", "PROJECT_MANAGER", "TEAM_LEADER")) {
            return true;
        }
        return isTaskAssignedToCurrentUser(attachment.getTask());
    }

    private void validateTaskAccess(Task task) {
        if (SecurityUtils.hasAnyRole("ADMIN", "PROJECT_MANAGER", "TEAM_LEADER")) {
            return;
        }
        if (!isTaskAssignedToCurrentUser(task)) {
            throw new AccessDeniedException();
        }
    }

    private boolean isTaskAssignedToCurrentUser(Task task) {
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (!StringUtils.hasText(currentUsername) || task == null || task.getAssignee() == null) {
            return false;
        }

        String assigneeEmail = task.getAssignee().getEmail();
        if (!StringUtils.hasText(assigneeEmail)) {
            return false;
        }

        return currentUsername.equalsIgnoreCase(assigneeEmail);
    }

    private String sanitizeFileName(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return "unnamed_file";
        }
        return fileName.trim().replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
    }

    private @NonNull Long requireId(@Nullable Long id, String fieldName) {
        return Objects.requireNonNull(id, fieldName + " is required");
    }
}

