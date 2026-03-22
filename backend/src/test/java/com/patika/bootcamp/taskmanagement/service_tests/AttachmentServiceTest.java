package com.patika.bootcamp.taskmanagement.service_tests;

import com.patika.bootcamp.taskmanagement.model.Attachment;
import com.patika.bootcamp.taskmanagement.model.Task;
import com.patika.bootcamp.taskmanagement.repository.AttachmentRepository;
import com.patika.bootcamp.taskmanagement.repository.TaskRepository;
import com.patika.bootcamp.taskmanagement.exception.AttachmentNotFoundException;
import com.patika.bootcamp.taskmanagement.service.impl.AttachmentServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "admin@example.com",
                        "N/A",
                        Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
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
        Assertions.assertThrows(AttachmentNotFoundException.class, () -> attachmentService.findById(99L));
    }

    @Test
    void shouldUploadAttachment() {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[1024]);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(attachmentRepository.save(anyAttachment())).thenReturn(requireAttachment(mockAttachment, "Mock attachment is required"));

        Attachment uploadedAttachment = attachmentService.upload(file, 1L);

        assertThat(uploadedAttachment.getFileName()).isEqualTo("test.pdf");
        verify(attachmentRepository, times(1)).save(anyAttachment());
    }

    @Test
    void shouldSoftDeleteAttachment() {
        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(mockAttachment));
        attachmentService.softDelete(1L);
        assertThat(mockAttachment.isDeleted()).isTrue();
        verify(attachmentRepository, times(1)).save(requireAttachment(mockAttachment, "Mock attachment is required"));
    }

    @Test
    void shouldUpdateAttachment() {
        Attachment updatedAttachment = new Attachment();
        updatedAttachment.setFileName("updated.pdf");

        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(mockAttachment));
        when(attachmentRepository.save(anyAttachment())).thenReturn(requireAttachment(mockAttachment, "Mock attachment is required"));

        Attachment result = attachmentService.update(1L, updatedAttachment);

        assertThat(result.getFileName()).isEqualTo("updated.pdf");
        verify(attachmentRepository, times(1)).save(requireAttachment(mockAttachment, "Mock attachment is required"));
    }

    private @NonNull Attachment requireAttachment(@Nullable Attachment attachment, String message) {
        return Objects.requireNonNull(attachment, message);
    }

    private @NonNull Attachment anyAttachment() {
        return any(Attachment.class);
    }
}

