package com.patika.bootcamp.taskmanagement.controller;

import com.patika.bootcamp.taskmanagement.model.Comment;
import com.patika.bootcamp.taskmanagement.service.CommentService;
import com.patika.bootcamp.taskmanagement.service.exception.CommentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        return ResponseEntity.ok(commentService.getAllComments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) throws CommentNotFoundException {
        return ResponseEntity.ok(commentService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment) {
        return new ResponseEntity<>(commentService.create(comment), HttpStatus.CREATED);
    }
}
