package com.patika.bootcamp.taskmanagement.integration_tests;

import com.patika.bootcamp.taskmanagement.model.Attachment;
import com.patika.bootcamp.taskmanagement.model.Task;
import com.patika.bootcamp.taskmanagement.model.TaskPriority;
import com.patika.bootcamp.taskmanagement.model.TaskState;
import com.patika.bootcamp.taskmanagement.repository.AttachmentRepository;
import com.patika.bootcamp.taskmanagement.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Her testten sonra DB temizlenir.
class AttachmentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private TaskRepository taskRepository;

    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setTitle("Test Task");
        testTask.setDescription("This is a test task");
        testTask.setState(TaskState.BACKLOG);
        testTask.setPriority(TaskPriority.MEDIUM);
        testTask = taskRepository.save(testTask);
    }

    @Test
    @WithMockUser(username = "team_member", roles = "TEAM_MEMBER")
    void shouldUploadAttachmentSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-file.txt",
                "text/plain",
                "Hello, World!".getBytes()
        );

        mockMvc.perform(multipart("/api/attachments")
                        .file(file)
                        .param("taskId", testTask.getId().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileName").value("test-file.txt"));

        assertThat(attachmentRepository.findAll()).hasSize(1);
    }

    @Test
    @WithMockUser(username = "team_member", roles = "TEAM_MEMBER")
    void shouldFailToUploadEmptyFile() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.txt",
                "text/plain",
                new byte[0]
        );

        mockMvc.perform(multipart("/api/attachments")
                        .file(emptyFile)
                        .param("taskId", testTask.getId().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest()); // ✅ 400 bekliyoruz
    }

    @Test
    void shouldFailToUploadAttachmentWithoutAuthentication() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "unauthorized.txt",
                "text/plain",
                "Unauthorized user".getBytes()
        );

        mockMvc.perform(multipart("/api/attachments")
                        .file(file)
                        .param("taskId", testTask.getId().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized()); // 401 beklenmeli
    }

    @Test
    @WithMockUser(username = "team_member", roles = "TEAM_MEMBER")
    void shouldFailToUploadFileToInvalidTask() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "invalid-task-file.txt",
                "text/plain",
                "Invalid Task".getBytes()
        );

        mockMvc.perform(multipart("/api/attachments")
                        .file(file)
                        .param("taskId", "99999")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound()); // 404 beklenmeli
    }

    @Test
    @WithMockUser(username = "team_member", roles = "TEAM_MEMBER")
    void shouldGetAttachmentById() throws Exception {
        Attachment attachment = new Attachment(null, "file.txt", "uploads/file.txt", "text/plain", 123L, testTask, LocalDateTime.now(), false);
        attachment = attachmentRepository.save(attachment);

        mockMvc.perform(get("/api/attachments/" + attachment.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("file.txt"));
    }

    @Test
    @WithMockUser(username = "team_member", roles = "TEAM_MEMBER")
    void shouldSoftDeleteAttachment() throws Exception {
        Attachment attachment = new Attachment(null, "file.txt", "uploads/file.txt", "text/plain", 123L, testTask, LocalDateTime.now(), false);
        attachment = attachmentRepository.save(attachment);

        mockMvc.perform(delete("/api/attachments/" + attachment.getId()))
                .andExpect(status().isNoContent());

        Attachment deletedAttachment = attachmentRepository.findById(attachment.getId()).orElseThrow();
        assertThat(deletedAttachment.isDeleted()).isTrue();
    }

    @Test
    @WithMockUser(username = "team_member", roles = "TEAM_MEMBER")
    void shouldReturnNotFoundForDeletedAttachment() throws Exception {
        Attachment attachment = new Attachment(null, "deleted-file.txt", "uploads/deleted-file.txt", "text/plain", 123L, testTask, LocalDateTime.now(), true);
        attachmentRepository.save(attachment);

        mockMvc.perform(get("/api/attachments/" + attachment.getId()))
                .andExpect(status().isNotFound());
    }
}