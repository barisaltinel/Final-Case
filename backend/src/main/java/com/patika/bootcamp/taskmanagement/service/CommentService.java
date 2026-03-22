package com.patika.bootcamp.taskmanagement.service;

import com.patika.bootcamp.taskmanagement.exception.CommentNotFoundException;
import com.patika.bootcamp.taskmanagement.model.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> getAllComments();
    Comment findById(Long id) throws CommentNotFoundException;
    Comment create(Comment comment);
}

