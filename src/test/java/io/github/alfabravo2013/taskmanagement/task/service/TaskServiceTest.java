package io.github.alfabravo2013.taskmanagement.task.service;

import io.github.alfabravo2013.taskmanagement.account.model.UserAccount;
import io.github.alfabravo2013.taskmanagement.account.service.UserAccountService;
import io.github.alfabravo2013.taskmanagement.common.exception.ResourceNotFoundException;
import io.github.alfabravo2013.taskmanagement.task.mapper.MockTaskView;
import io.github.alfabravo2013.taskmanagement.task.model.Task;
import io.github.alfabravo2013.taskmanagement.task.model.TaskPriority;
import io.github.alfabravo2013.taskmanagement.task.model.TaskStatus;
import io.github.alfabravo2013.taskmanagement.task.model.TaskView;
import io.github.alfabravo2013.taskmanagement.task.repository.TaskRepository;
import io.github.alfabravo2013.taskmanagement.task.web.TaskAssigneeChangeRequest;
import io.github.alfabravo2013.taskmanagement.task.web.TaskCreateRequest;
import io.github.alfabravo2013.taskmanagement.task.web.TaskStatusChangeRequest;
import io.github.alfabravo2013.taskmanagement.task.web.TaskUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserAccountService userAccountService;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    @DisplayName("When receives a TaskCreateRequest, returns a proper new Task entity")
    void createTaskEntity() {
        var request = new TaskCreateRequest("title", "description", TaskPriority.LOW);

        var user = new UserAccount();
        user.setId(1L);

        var task = new Task();
        task.setId(1L);
        task.setAuthor(user);

        when(userAccountService.getCurrentUser()).thenReturn(user);
        when(taskRepository.save(any())).thenReturn(task);

        var actual = taskService.createTask(request);

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getAuthor()).isNotNull();
        assertThat(actual.getAuthor().getId()).isEqualTo(user.getId());

        verify(userAccountService, times(1)).getCurrentUser();
        verify(taskRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("When id is valid, return the Task")
    void findTaskById() {
        var taskId = 1L;
        var task = new Task();
        task.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        var actual = taskService.findTask(taskId);

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(taskId);

        verify(userAccountService, never()).getCurrentUser();
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    @DisplayName("When id is invalid, throw ResourceNotFound")
    void throwWhenFindingTaskById() {
        var taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findTask(taskId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userAccountService, never()).getCurrentUser();
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    @DisplayName("When id is invalid, throw ResourceNotFound")
    void throwWhenUpdatingTaskById() {
        var taskId = 2L;
        var title = "title";
        var description = "description";
        var priority = TaskPriority.LOW;
        var status = TaskStatus.WAITING;

        var request = new TaskUpdateRequest(title, description, priority, status);

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(taskId, request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userAccountService, never()).getCurrentUser();
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    @DisplayName("When current user is not the author, throw AccessDenied")
    void throwWhenUpdatingByUnauthorizedUser() {
        var userId = 1L;
        var user = new UserAccount();
        user.setId(userId);

        var authorId = 2L;
        var author = new UserAccount();
        author.setId(authorId);

        var taskId = 2L;
        var title = "title";
        var description = "description";
        var priority = TaskPriority.LOW;
        var status = TaskStatus.WAITING;

        var request = new TaskUpdateRequest(title, description, priority, status);

        var task = new Task();
        task.setId(taskId);
        task.setAuthor(author);

        when(userAccountService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.updateTask(taskId, request))
                .isInstanceOf(AccessDeniedException.class);

        verify(userAccountService, times(1)).getCurrentUser();
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    @DisplayName("When taskId is correct then delete the Task")
    void deleteTaskById() {
        var userId = 1L;
        var user = new UserAccount();
        user.setId(userId);

        var taskId = 2L;
        var task = new Task();
        task.setId(taskId);
        task.setAuthor(user);

        when(userAccountService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        taskService.deleteTask(taskId);

        verify(userAccountService, times(1)).getCurrentUser();
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    @DisplayName("When taskId is incorrect then throw ResourceNotFound")
    void throwWhenDeletingTaskById() {
        var taskId = 2L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask(taskId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userAccountService, never()).getCurrentUser();
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).deleteById(taskId);
    }

    @Test
    @DisplayName("When current user is not the author then throw AccessDenied")
    void throwWhenDeletingByUnauthorizedUser() {
        var userId = 1L;
        var user = new UserAccount();
        user.setId(userId);

        var authorId = 2L;
        var author = new UserAccount();
        author.setId(authorId);

        var taskId = 3L;
        var task = new Task();
        task.setId(taskId);
        task.setAuthor(author);

        when(userAccountService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.deleteTask(taskId))
                .isInstanceOf(AccessDeniedException.class);

        verify(userAccountService, times(1)).getCurrentUser();
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).deleteById(taskId);
    }

    @Test
    @DisplayName("When taskId and assigneeId are valid and assignee is null return updated Task")
    void setAssigneeByTaskId() {
        var userId = 1L;
        var user = new UserAccount();
        user.setId(userId);

        var assigneeId = 2L;
        var assignee = new UserAccount();
        assignee.setId(assigneeId);

        var taskId = 3L;
        var task = new Task();
        task.setId(taskId);
        task.setAuthor(user);

        var request = new TaskAssigneeChangeRequest(assigneeId);

        when(userAccountService.getCurrentUser()).thenReturn(user);
        when(userAccountService.findUserById(assigneeId)).thenReturn(assignee);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenReturn(task);

        var actual = taskService.assignTask(taskId, request);

        assertThat(actual).isNotNull();
        assertThat(actual.getAssignee()).isNotNull();
        assertThat(actual.getAssignee().getId()).isEqualTo(assigneeId);

        verify(userAccountService, times(1)).getCurrentUser();
        verify(userAccountService, times(1)).findUserById(assigneeId);
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("When taskId and assigneeId are valid and assignee is not null return updated Task")
    void changeAssigneeByTaskId() {
        var userId = 1L;
        var user = new UserAccount();
        user.setId(userId);

        var assigneeId = 2L;
        var assignee = new UserAccount();
        assignee.setId(assigneeId);

        var previousAssigneeId = 3L;
        var previousAssignee = new UserAccount();
        previousAssignee.setId(previousAssigneeId);

        var taskId = 3L;
        var task = new Task();
        task.setId(taskId);
        task.setAuthor(user);
        task.setAssignee(previousAssignee);

        var updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setAuthor(user);
        updatedTask.setAssignee(assignee);

        var request = new TaskAssigneeChangeRequest(assigneeId);

        when(userAccountService.getCurrentUser()).thenReturn(user);
        when(userAccountService.findUserById(assigneeId)).thenReturn(assignee);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenReturn(updatedTask);

        var actual = taskService.assignTask(taskId, request);

        assertThat(actual).isNotNull();
        assertThat(actual.getAssignee()).isNotNull();
        assertThat(actual.getAssignee().getId()).isEqualTo(assigneeId);

        verify(userAccountService, times(1)).getCurrentUser();
        verify(userAccountService, times(1)).findUserById(assigneeId);
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("When current user is not the author then throw AccessDenied")
    void throwWhenAssigningByUnauthorizedUser() {
        var userId = 1L;
        var user = new UserAccount();
        user.setId(userId);

        var authorId = 2L;
        var author = new UserAccount();
        author.setId(authorId);

        var assigneeId = 3L;

        var taskId = 4L;
        var task = new Task();
        task.setId(taskId);
        task.setAuthor(author);

        var request = new TaskAssigneeChangeRequest(assigneeId);

        when(userAccountService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.assignTask(taskId, request))
                .isInstanceOf(AccessDeniedException.class);

        verify(userAccountService, times(1)).getCurrentUser();
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("When taskId is valid and the user is the author return Task with updated status")
    void updateTaskStatusByAuthor() {
        var userId = 1L;
        var user = new UserAccount();
        user.setId(userId);

        var taskId = 2L;
        var task = new Task();
        task.setId(taskId);
        task.setAuthor(user);

        var status = TaskStatus.COMPLETED;

        var request = new TaskStatusChangeRequest(status);

        var updatedTask = new Task();
        updatedTask.setStatus(status);
        updatedTask.setId(taskId);
        updatedTask.setAuthor(user);

        when(userAccountService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenReturn(updatedTask);

        var actual = taskService.updateStatus(taskId, request);

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(taskId);
        assertThat(actual.getAuthor().getId()).isEqualTo(userId);
        assertThat(actual.getStatus()).isEqualTo(status);

        verify(userAccountService, times(1)).getCurrentUser();
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("When taskId is valid and the user is the assignee return Task with updated status")
    void updateTaskStatusByAssignee() {
        var userId = 1L;
        var user = new UserAccount();
        user.setId(userId);

        var authorId = 2L;
        var author = new UserAccount();
        author.setId(authorId);

        var taskId = 3L;
        var task = new Task();
        task.setId(taskId);
        task.setAuthor(author);
        task.setAssignee(user);

        var status = TaskStatus.COMPLETED;

        var request = new TaskStatusChangeRequest(status);

        var updatedTask = new Task();
        updatedTask.setStatus(status);
        updatedTask.setId(taskId);
        updatedTask.setAssignee(user);

        when(userAccountService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenReturn(updatedTask);

        var actual = taskService.updateStatus(taskId, request);

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(taskId);
        assertThat(actual.getAssignee().getId()).isEqualTo(userId);
        assertThat(actual.getStatus()).isEqualTo(status);

        verify(userAccountService, times(1)).getCurrentUser();
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("When taskId is invalid throw ResourceNotFound")
    void throwUpdatingTaskStatusById() {
        var taskId = 2L;

        var status = TaskStatus.COMPLETED;
        var request = new TaskStatusChangeRequest(status);

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateStatus(taskId, request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userAccountService, never()).getCurrentUser();
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("When taskId is invalid throw ResourceNotFound")
    void throwUpdatingTaskByUnauthorizedUser() {
        var userId = 1L;
        var user = new UserAccount();
        user.setId(userId);

        var authorId = 2L;
        var author = new UserAccount();
        author.setId(authorId);

        var assigneeId = 3L;
        var assignee = new UserAccount();
        assignee.setId(assigneeId);

        var taskId = 1L;
        var task = new Task();
        task.setId(taskId);
        task.setAuthor(author);
        task.setAssignee(assignee);

        var status = TaskStatus.COMPLETED;
        var request = new TaskStatusChangeRequest(status);

        when(userAccountService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.updateStatus(taskId, request))
                .isInstanceOf(AccessDeniedException.class);

        verify(userAccountService, times(1)).getCurrentUser();
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("When filters are empty, invoke findAllTaskViews()")
    void invokeFindAllTasks() {
        Map<String, Long> filters = Map.of();
        var taskView1 = new MockTaskView(1L, 1L, TaskStatus.WAITING,
                TaskPriority.LOW, 2L, 0);
        var taskView2 = new MockTaskView(2L, 1L, TaskStatus.WAITING,
                TaskPriority.HIGH, 3L, 2);
        List<TaskView> content = List.of(taskView1, taskView2);
        Page<TaskView> page = new PageImpl<>(content);

        when(taskRepository.findAllTaskViews(any())).thenReturn(page);

        var actual = taskService.getTasks(filters, 1, 25);

        assertThat(actual).isNotNull();
        assertThat(actual.getContent()).hasSameElementsAs(content);

        verify(taskRepository, times(1)).findAllTaskViews(any());
        verify(taskRepository, never()).findTaskViewsByAssigneeId(any(), any());
        verify(taskRepository, never()).findTaskViewsByAuthorId(any(), any());
    }

    @Test
    @DisplayName("When filters has authorId, invoke findAllTaskViewsByAuthorId()")
    void invokeFindAllTaskViewsByAuthorId() {
        Long authorId = 1L;
        Map<String, Long> filters = Map.of("authorId", authorId);
        var taskView = new MockTaskView(1L, 1L, TaskStatus.WAITING,
                TaskPriority.LOW, 2L, 0);
        List<TaskView> content = List.of(taskView);
        Page<TaskView> page = new PageImpl<>(content);

        when(taskRepository.findTaskViewsByAuthorId(anyLong(), any())).thenReturn(page);

        var actual = taskService.getTasks(filters, 1, 25);

        assertThat(actual).isNotNull();
        assertThat(actual.getContent()).hasSameElementsAs(content);

        verify(taskRepository, never()).findAllTaskViews(any());
        verify(taskRepository, never()).findTaskViewsByAssigneeId(any(), any());
        verify(taskRepository, times(1)).findTaskViewsByAuthorId(anyLong(), any());
    }

    @Test
    @DisplayName("When filters has assigneeId, invoke findAllTaskViewsByAssigneeId()")
    void invokeFindAllTaskViewsByAssigneeId() {
        Long assigneeId = 1L;
        Map<String, Long> filters = Map.of("assigneeId", assigneeId);
        var taskView = new MockTaskView(1L, 1L, TaskStatus.WAITING,
                TaskPriority.LOW, 2L, 0);
        List<TaskView> content = List.of(taskView);
        Page<TaskView> page = new PageImpl<>(content);

        when(taskRepository.findTaskViewsByAssigneeId(anyLong(), any())).thenReturn(page);

        var actual = taskService.getTasks(filters, 1, 25);

        assertThat(actual).isNotNull();
        assertThat(actual.getContent()).hasSameElementsAs(content);

        verify(taskRepository, never()).findAllTaskViews(any());
        verify(taskRepository, never()).findTaskViewsByAuthorId(any(), any());
        verify(taskRepository, times(1)).findTaskViewsByAssigneeId(anyLong(), any());
    }
}
