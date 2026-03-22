package com.patika.bootcamp.taskmanagement.service.impl;

import com.patika.bootcamp.taskmanagement.model.Comment;
import com.patika.bootcamp.taskmanagement.model.Task;
import com.patika.bootcamp.taskmanagement.model.User;
import com.patika.bootcamp.taskmanagement.repository.CommentRepository;
import com.patika.bootcamp.taskmanagement.repository.TaskRepository;
import com.patika.bootcamp.taskmanagement.repository.UserRepository;
import com.patika.bootcamp.taskmanagement.service.CommentService;
import com.patika.bootcamp.taskmanagement.exception.AccessDeniedException;
import com.patika.bootcamp.taskmanagement.exception.CommentNotFoundException;
import com.patika.bootcamp.taskmanagement.exception.TaskNotFoundException;
import com.patika.bootcamp.taskmanagement.exception.UserNotFoundException;
import com.patika.bootcamp.taskmanagement.util.SecurityUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Comment> getAllComments() {
        return commentRepository.findAll().stream()
                .filter(this::canAccessComment)
                .toList();
    }

    @Override
    public Comment findById(Long id) {
        Long requiredId = requireId(id, "Comment id");

        Comment comment = commentRepository.findById(requiredId)
                .orElseThrow(CommentNotFoundException::new);
        if (!canAccessComment(comment)) {
            throw new AccessDeniedException();
        }
        return comment;
    }

    @Override
    public Comment create(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("Comment details are required");
        }
        Task commentTask = comment.getTask();
        Long taskId = commentTask != null ? commentTask.getId() : null;
        if (taskId == null) {
            throw new IllegalArgumentException("Task id is required");
        }
        if (!StringUtils.hasText(comment.getText())) {
            throw new IllegalArgumentException("Comment text cannot be empty");
        }

        String currentUsername = SecurityUtils.getCurrentUsername();
        if (!StringUtils.hasText(currentUsername)) {
            throw new AccessDeniedException();
        }

        User author = userRepository.findByEmailAndDeletedFalse(currentUsername)
                .orElseThrow(UserNotFoundException::new);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(TaskNotFoundException::new);

        if (!canAccessTask(task)) {
            throw new AccessDeniedException();
        }

        Comment newComment = new Comment();
        newComment.setText(comment.getText().trim());
        newComment.setAuthor(author);
        newComment.setTask(task);
        newComment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(newComment);
    }

    private boolean canAccessComment(Comment comment) {
        if (SecurityUtils.hasAnyRole("ADMIN", "PROJECT_MANAGER", "TEAM_LEADER")) {
            return true;
        }
        return canAccessTask(comment.getTask());
    }

    private boolean canAccessTask(Task task) {
        if (SecurityUtils.hasAnyRole("ADMIN", "PROJECT_MANAGER", "TEAM_LEADER")) {
            return true;
        }
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

    private @NonNull Long requireId(@Nullable Long id, String fieldName) {
        return Objects.requireNonNull(id, fieldName + " is required");
    }
}

