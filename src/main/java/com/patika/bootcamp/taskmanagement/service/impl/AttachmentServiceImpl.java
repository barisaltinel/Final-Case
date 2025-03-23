package com.patika.bootcamp.taskmanagement.service.impl;

import com.patika.bootcamp.taskmanagement.model.Attachment;
import com.patika.bootcamp.taskmanagement.model.Task;
import com.patika.bootcamp.taskmanagement.repository.AttachmentRepository;
import com.patika.bootcamp.taskmanagement.repository.TaskRepository;
import com.patika.bootcamp.taskmanagement.service.AttachmentService;
import com.patika.bootcamp.taskmanagement.service.exception.AttachmentNotFoundException;
import com.patika.bootcamp.taskmanagement.service.exception.EmptyFileException;
import com.patika.bootcamp.taskmanagement.service.exception.TaskNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private static final String UPLOAD_DIR = "uploads/";

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository, TaskRepository taskRepository) {
        this.attachmentRepository = attachmentRepository;
        this.taskRepository = taskRepository;
    }

    /** ✅ Silinmemiş (deleted=false) tüm dosyaları getir */
    @Override
    public List<Attachment> getAllAttachments() {
        return attachmentRepository.findAll().stream()
                .filter(attachment -> !attachment.isDeleted())
                .toList();
    }

    /** ✅ Silinmemiş (deleted=false) bir dosyayı ID ile getir */
    @Override
    public Attachment findById(Long id) {
        return attachmentRepository.findById(id)
                .filter(attachment -> !attachment.isDeleted()) 
                .orElseThrow(() -> new AttachmentNotFoundException());
    }

    /** ✅ Yeni bir dosya yükle */
    @Override
    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'TEAM_LEADER', 'PROJECT_MANAGER', 'ADMIN')")
    public Attachment upload(MultipartFile file, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException());

        if (file.isEmpty()) {
            throw new EmptyFileException();
        }

        String originalFileName = sanitizeFileName(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + "_" + originalFileName;
        String filePath = UPLOAD_DIR + fileName;

        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }

        Attachment attachment = new Attachment();
        attachment.setFileName(originalFileName);
        attachment.setFilePath(filePath);
        attachment.setMimeType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setTask(task);
        attachment.setUploadedAt(LocalDateTime.now());
        attachment.setDeleted(false);

        return attachmentRepository.save(attachment);
    }

    /** ✅ Dosya bilgilerini güncelle */
    @Override
    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'TEAM_LEADER', 'PROJECT_MANAGER', 'ADMIN')")
    public Attachment update(Long id, Attachment updatedAttachment) {
        Attachment existingAttachment = findById(id);
        existingAttachment.setFileName(sanitizeFileName(updatedAttachment.getFileName()));
        existingAttachment.setFilePath(updatedAttachment.getFilePath());
        existingAttachment.setMimeType(updatedAttachment.getMimeType());
        existingAttachment.setFileSize(updatedAttachment.getFileSize());
        return attachmentRepository.save(existingAttachment);
    }

    /** ✅ Soft delete işlemi: Dosya silinmiş olarak işaretlenir */
    @Override
    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'TEAM_LEADER', 'PROJECT_MANAGER', 'ADMIN')")
    public void softDelete(Long id) {
        Attachment attachment = findById(id);
        attachment.markAsDeleted();
        attachmentRepository.save(attachment);
    }

    /** 📌 Güvenlik için dosya adını temizleyen yardımcı metod */
    private String sanitizeFileName(String fileName) {
        if (fileName == null) return "unnamed_file";
        return fileName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
    }
}
