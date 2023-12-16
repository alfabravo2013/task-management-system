package io.github.alfabravo2013.taskmanagement.comment.service;

import io.github.alfabravo2013.taskmanagement.account.service.UserAccountService;
import io.github.alfabravo2013.taskmanagement.comment.model.Comment;
import io.github.alfabravo2013.taskmanagement.comment.repository.CommentRepository;
import io.github.alfabravo2013.taskmanagement.comment.web.CommentCreateRequest;
import io.github.alfabravo2013.taskmanagement.task.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserAccountService userAccountService;
    private final TaskService taskService;

    public CommentServiceImpl(CommentRepository commentRepository,
                              UserAccountService userAccountService,
                              TaskService taskService) {
        this.commentRepository = commentRepository;
        this.userAccountService = userAccountService;
        this.taskService = taskService;
    }

    @Override
    @Transactional
    public void createComment(Long taskId, CommentCreateRequest request) {
        var task = taskService.findTask(taskId);
        var commentAuthor = userAccountService.getCurrentUser();

        var comment = new Comment();
        comment.setText(request.text());
        comment.setAuthor(commentAuthor);
        comment.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        comment.setTask(task);

        commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Comment> findCommentsByTaskId(Long taskId, int page, int perPage) {
        Sort sort = Sort.by("createdAt").descending();
        PageRequest pageRequest = PageRequest.of(page, perPage, sort);
        return commentRepository.findAllByTaskId(taskId, pageRequest);
    }
}
