package com.patika.bootcamp.taskmanagement.controller_tests;

import com.patika.bootcamp.taskmanagement.controller.AttachmentController;
import com.patika.bootcamp.taskmanagement.model.Attachment;
import com.patika.bootcamp.taskmanagement.service.AttachmentService;
import com.patika.bootcamp.taskmanagement.service.exception.AttachmentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentControllerTest {

    @Mock
    private AttachmentService attachmentService;

    @InjectMocks
    private AttachmentController attachmentController;

    private Attachment mockAttachment;

    @BeforeEach
    void setUp() {
        mockAttachment = new Attachment();
        mockAttachment.setId(1L);
        mockAttachment.setFileName("test-file.txt");
        mockAttachment.setFilePath("uploads/test-file.txt");
        mockAttachment.setMimeType("text/plain");
        mockAttachment.setFileSize(1024L);
    }

    @Test
    @WithMockUser
    void shouldReturnAllAttachments() {
        when(attachmentService.getAllAttachments()).thenReturn(List.of(mockAttachment));
        ResponseEntity<List<Attachment>> response = attachmentController.getAllAttachments();
        assertThat(response.getBody()).isNotNull().hasSize(1);
        assertThat(response.getBody().get(0)).isEqualTo(mockAttachment);
    }

    @Test
    @WithMockUser
    void shouldReturnAttachmentById() {
        when(attachmentService.findById(1L)).thenReturn(mockAttachment);
        ResponseEntity<Attachment> response = attachmentController.getAttachmentById(1L);
        assertThat(response.getBody()).isNotNull().isEqualTo(mockAttachment);
    }

    @Test
    @WithMockUser
    void shouldThrowExceptionWhenAttachmentNotFound() {
        when(attachmentService.findById(99L)).thenThrow(new AttachmentNotFoundException());
        assertThatThrownBy(() -> attachmentController.getAttachmentById(99L))
                .isInstanceOf(AttachmentNotFoundException.class)
                .hasMessageContaining("Attachment not found");
    }

    @Test
    @WithMockUser(username = "team_member", roles = "TEAM_MEMBER")
    void shouldUploadFileSuccessfully() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-file.txt", "text/plain", "Hello, World!".getBytes()
        );
        when(attachmentService.upload(any(), anyLong())).thenReturn(mockAttachment);
        ResponseEntity<Attachment> response = attachmentController.uploadAttachment(file, 1L);
        assertThat(response.getBody()).isNotNull().isEqualTo(mockAttachment);
    }

    @Test
    @WithMockUser(username = "team_member", roles = "TEAM_MEMBER")
    void shouldSoftDeleteAttachment() {
        doNothing().when(attachmentService).softDelete(1L);
        ResponseEntity<Void> response = attachmentController.softDeleteAttachment(1L);
        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }
}
