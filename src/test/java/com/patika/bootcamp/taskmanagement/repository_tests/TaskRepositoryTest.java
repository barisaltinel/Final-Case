package com.patika.bootcamp.taskmanagement.repository_tests;

import com.patika.bootcamp.taskmanagement.model.Task;
import com.patika.bootcamp.taskmanagement.model.TaskPriority;
import com.patika.bootcamp.taskmanagement.model.TaskState;
import com.patika.bootcamp.taskmanagement.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("Valid description");
        task1.setPriority(TaskPriority.HIGH);
        task1.setState(TaskState.BACKLOG);

        task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Valid description");
        task2.setPriority(TaskPriority.MEDIUM);
        task2.setState(TaskState.CANCELLED);

        taskRepository.save(task1);
        taskRepository.save(task2);
    }

    @Test
    void shouldFindAllTasksExceptCancelled() {
        List<Task> tasks = taskRepository.findAllByStateNot(TaskState.CANCELLED);

        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getTitle()).isEqualTo("Task 1");
    }
}
