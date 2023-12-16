package io.github.alfabravo2013.taskmanagement.comment.service;

import io.github.alfabravo2013.taskmanagement.comment.model.Comment;
import io.github.alfabravo2013.taskmanagement.comment.web.CommentCreateRequest;
import org.springframework.data.domain.Page;

public interface CommentService {
    void createComment(Long taskId, CommentCreateRequest request);

    Page<Comment> findCommentsByTaskId(Long taskId, int page, int perPage);
}
