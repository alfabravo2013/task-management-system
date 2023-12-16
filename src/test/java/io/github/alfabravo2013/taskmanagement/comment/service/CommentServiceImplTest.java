package io.github.alfabravo2013.taskmanagement.comment.service;

import io.github.alfabravo2013.taskmanagement.account.model.UserAccount;
import io.github.alfabravo2013.taskmanagement.account.service.UserAccountService;
import io.github.alfabravo2013.taskmanagement.comment.model.Comment;
import io.github.alfabravo2013.taskmanagement.comment.repository.CommentRepository;
import io.github.alfabravo2013.taskmanagement.comment.web.CommentCreateRequest;
import io.github.alfabravo2013.taskmanagement.common.exception.ResourceNotFoundException;
import io.github.alfabravo2013.taskmanagement.task.model.Task;
import io.github.alfabravo2013.taskmanagement.task.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserAccountService userAccountService;
    @Mock
    private TaskService taskService;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    @DisplayName("When taskId is valid run successfully")
    void createComment() {
        var userId = 1L;
        var user = new UserAccount();
        user.setId(userId);

        var taskId = 2L;
        var task = new Task();
        task.setId(taskId);

        var text = "comment";
        var request = new CommentCreateRequest(text);

        when(taskService.findTask(taskId)).thenReturn(task);
        when(userAccountService.getCurrentUser()).thenReturn(user);
        when(commentRepository.save(any())).thenReturn(new Comment());

        commentService.createComment(taskId, request);

        verify(taskService, times(1)).findTask(taskId);
        verify(userAccountService, times(1)).getCurrentUser();
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("When taskId is not valid throw ResourceNotFound")
    void throwIfTaskIdIsNotValid() {
        var taskId = 2L;

        var text = "comment";
        var request = new CommentCreateRequest(text);

        when(taskService.findTask(taskId)).thenThrow(new ResourceNotFoundException("msg"));

        assertThatThrownBy(() -> commentService.createComment(taskId, request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(taskService, times(1)).findTask(taskId);
        verify(userAccountService, never()).getCurrentUser();
        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("When taskId is valid return page of Comments")
    void findCommentsByTaskId() {
        var comment1 = new Comment();
        var comment2 = new Comment();
        List<Comment> content = List.of(comment1, comment2);
        Page<Comment> page = new PageImpl<>(content);

        var taskId = 1L;

        when(commentRepository.findAllByTaskId(anyLong(), any())).thenReturn(page);

        var actual = commentService.findCommentsByTaskId(taskId, 1, 25);

        assertThat(actual).isNotNull();
        assertThat(actual.getContent()).hasSameElementsAs(content);

        verify(commentRepository, times(1)).findAllByTaskId(anyLong(), any());
    }

    @Test
    @DisplayName("When taskId is not valid return empty page")
    void throwIfCommentIdIsNotValid() {
        var taskId = 1L;
        Page<Comment> page = new PageImpl<>(List.of());

        when(commentRepository.findAllByTaskId(anyLong(), any())).thenReturn(page);

        var actual = commentService.findCommentsByTaskId(taskId, 1, 25);

        assertThat(actual).isNotNull();
        assertThat(actual.getContent()).isEmpty();

        verify(commentRepository, times(1)).findAllByTaskId(anyLong(), any());
    }
}
