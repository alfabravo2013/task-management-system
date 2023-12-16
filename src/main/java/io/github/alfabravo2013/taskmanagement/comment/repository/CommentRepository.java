package io.github.alfabravo2013.taskmanagement.comment.repository;

import io.github.alfabravo2013.taskmanagement.comment.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByTaskId(Long taskId, Pageable pageable);
}
