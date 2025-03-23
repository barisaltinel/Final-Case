package com.patika.bootcamp.taskmanagement.service_tests;

import com.patika.bootcamp.taskmanagement.model.Attachment;
import com.patika.bootcamp.taskmanagement.model.Task;
import com.patika.bootcamp.taskmanagement.repository.AttachmentRepository;
import com.patika.bootcamp.taskmanagement.repository.TaskRepository;
import com.patika.bootcamp.taskmanagement.service.exception.AttachmentNotFoundException;
import com.patika.bootcamp.taskmanagement.service.impl.AttachmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.junit.jupiter.api.Assertions;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private AttachmentRepository attachmentRepository;
    
    @Mock
    private TaskRepository taskRepository;
    
    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    private Attachment mockAttachment;
    private Task mockTask;

    @BeforeEach
    void setUp() {
        mockTask = new Task();
        mockTask.setId(1L);
        mockAttachment = new Attachment();
        mockAttachment.setId(1L);
        mockAttachment.setFileName("test.pdf");
        mockAttachment.setFilePath("uploads/test.pdf");
        mockAttachment.setMimeType("application/pdf");
        mockAttachment.setFileSize(1024L);
        mockAttachment.setTask(mockTask);
        mockAttachment.setDeleted(false);
    }

    @Test
    void shouldReturnAllAttachments() {
        when(attachmentRepository.findAll()).thenReturn(List.of(mockAttachment));

        List<Attachment> attachments = attachmentService.getAllAttachments();

        assertThat(attachments).hasSize(1);
    }

    @Test
    void shouldFindAttachmentById() {
        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(mockAttachment));

        Attachment attachment = attachmentService.findById(1L);

        assertThat(attachment).isEqualTo(mockAttachment);
    }

    @Test
    void shouldThrowAttachmentNotFoundException() {
        when(attachmentRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertThrows(AttachmentNotFoundException.class, () -> 
            attachmentService.findById(99L)
        );
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldUploadAttachment() {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[1024]);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(mockAttachment);

        Attachment uploadedAttachment = attachmentService.upload(file, 1L);

        assertThat(uploadedAttachment.getFileName()).isEqualTo("test.pdf");
        verify(attachmentRepository, times(1)).save(any(Attachment.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldSoftDeleteAttachment() {
        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(mockAttachment));

        attachmentService.softDelete(1L);

        assertThat(mockAttachment.isDeleted()).isTrue();
        verify(attachmentRepository, times(1)).save(mockAttachment);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldUpdateAttachment() {
        Attachment updatedAttachment = new Attachment();
        updatedAttachment.setFileName("updated.pdf");
        updatedAttachment.setFilePath("uploads/updated.pdf");
        updatedAttachment.setMimeType("application/pdf");
        updatedAttachment.setFileSize(2048L);

        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(mockAttachment));
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(updatedAttachment);

        Attachment result = attachmentService.update(1L, updatedAttachment);

        assertThat(result.getFileName()).isEqualTo("updated.pdf");
        verify(attachmentRepository, times(1)).save(mockAttachment);
    }


}
