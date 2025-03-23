package com.patika.bootcamp.taskmanagement.service.impl;

import com.patika.bootcamp.taskmanagement.service.exception.CommentNotFoundException;
import com.patika.bootcamp.taskmanagement.model.Comment;
import com.patika.bootcamp.taskmanagement.repository.CommentRepository;
import com.patika.bootcamp.taskmanagement.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @Override
    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(CommentNotFoundException::new);
    }

    @Override
    public Comment create(Comment comment) {
        return commentRepository.save(comment);
    }
}
