package com.patika.bootcamp.taskmanagement.repository_tests;

import com.patika.bootcamp.taskmanagement.model.Attachment;
import com.patika.bootcamp.taskmanagement.model.Task;
import com.patika.bootcamp.taskmanagement.model.TaskPriority;
import com.patika.bootcamp.taskmanagement.model.TaskState;
import com.patika.bootcamp.taskmanagement.repository.AttachmentRepository;
import com.patika.bootcamp.taskmanagement.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class AttachmentRepositoryTest {

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
    void shouldSaveAndRetrieveAttachment() {
        Attachment attachment = new Attachment(null, "file.txt", "uploads/file.txt", "text/plain", 123L, testTask, LocalDateTime.now(), false);
        Attachment savedAttachment = attachmentRepository.save(attachment);

        Optional<Attachment> foundAttachment = attachmentRepository.findById(savedAttachment.getId());
        assertThat(foundAttachment).isPresent();
        assertThat(foundAttachment.get().getFileName()).isEqualTo("file.txt");
    }

    @Test
    void shouldSoftDeleteAttachment() {
        Attachment attachment = new Attachment(null, "file.txt", "uploads/file.txt", "text/plain", 123L, testTask, LocalDateTime.now(), false);
        Attachment savedAttachment = attachmentRepository.save(attachment);

        savedAttachment.setDeleted(true);
        attachmentRepository.save(savedAttachment);

        Optional<Attachment> foundAttachment = attachmentRepository.findById(savedAttachment.getId());
        assertThat(foundAttachment).isPresent();
        assertThat(foundAttachment.get().isDeleted()).isTrue();
    }

    @Test
    void shouldNotFindDeletedAttachment() {
        Attachment attachment = new Attachment(null, "file.txt", "uploads/file.txt", "text/plain", 123L, testTask, LocalDateTime.now(), true);
        attachmentRepository.save(attachment);

        Optional<Attachment> foundAttachment = attachmentRepository.findById(attachment.getId());
        assertThat(foundAttachment).isPresent();
        assertThat(foundAttachment.get().isDeleted()).isTrue();
    }

    @Test
    void shouldFindAllAttachmentsByTaskId() {
        Attachment attachment1 = new Attachment(null, "file1.txt", "uploads/file1.txt", "text/plain", 123L, testTask, LocalDateTime.now(), false);
        Attachment attachment2 = new Attachment(null, "file2.txt", "uploads/file2.txt", "text/plain", 456L, testTask, LocalDateTime.now(), false);
        attachmentRepository.saveAll(List.of(attachment1, attachment2));

        List<Attachment> attachments = attachmentRepository.findAll();
        assertThat(attachments).hasSize(2);
    }

    @Test
    void shouldNotFindDeletedAttachmentsInList() {
        Attachment attachment1 = new Attachment(null, "file1.txt", "uploads/file1.txt", "text/plain", 123L, testTask, LocalDateTime.now(), false);
        Attachment attachment2 = new Attachment(null, "file2.txt", "uploads/file2.txt", "text/plain", 456L, testTask, LocalDateTime.now(), true);
        attachmentRepository.saveAll(List.of(attachment1, attachment2));

        List<Attachment> attachments = attachmentRepository.findAll().stream()
                .filter(a -> !a.isDeleted())
                .toList();

        assertThat(attachments).hasSize(1);
        assertThat(attachments.get(0).getFileName()).isEqualTo("file1.txt");
    }

    @Test
    void shouldReturnEmptyListIfNoAttachmentsExist() {
        List<Attachment> attachments = attachmentRepository.findAll();
        assertThat(attachments).isEmpty();
    }
}