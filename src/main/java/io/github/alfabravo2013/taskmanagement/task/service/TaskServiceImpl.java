package io.github.alfabravo2013.taskmanagement.task.service;

import io.github.alfabravo2013.taskmanagement.account.service.UserAccountService;
import io.github.alfabravo2013.taskmanagement.common.exception.ResourceNotFoundException;
import io.github.alfabravo2013.taskmanagement.task.model.Task;
import io.github.alfabravo2013.taskmanagement.task.model.TaskStatus;
import io.github.alfabravo2013.taskmanagement.task.model.TaskView;
import io.github.alfabravo2013.taskmanagement.task.repository.TaskRepository;
import io.github.alfabravo2013.taskmanagement.task.web.TaskAssigneeChangeRequest;
import io.github.alfabravo2013.taskmanagement.task.web.TaskCreateRequest;
import io.github.alfabravo2013.taskmanagement.task.web.TaskStatusChangeRequest;
import io.github.alfabravo2013.taskmanagement.task.web.TaskUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Service
@Slf4j
@CacheConfig(cacheNames = "taskCache")
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserAccountService userAccountService;

    public TaskServiceImpl(TaskRepository taskRepository,
                           UserAccountService userAccountService) {
        this.taskRepository = taskRepository;
        this.userAccountService = userAccountService;
    }

    @Override
    @Transactional
    public Task createTask(TaskCreateRequest request) {
        var author = userAccountService.getCurrentUser();

        var task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(TaskStatus.WAITING);
        task.setPriority(request.priority());
        task.setAuthor(author);
        task.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));

        return taskRepository.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskView> getTasks(Map<String, Long> filters, int page, int perPage) {
        Sort sort = Sort.by("created_at").descending();
        PageRequest pageRequest = PageRequest.of(page, perPage, sort);

        Long authorId = filters.get("authorId");
        if (authorId != null) {
            return taskRepository.findTaskViewsByAuthorId(authorId, pageRequest);
        }

        Long assigneeId = filters.get("assigneeId");
        if (assigneeId != null) {
            return taskRepository.findTaskViewsByAssigneeId(assigneeId, pageRequest);
        }

        return taskRepository.findAllTaskViews(pageRequest);
    }

    @Override
    @Cacheable(key = "#taskId")
    public Task findTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.trace("Задача id={} не найдена", taskId);
                    return new ResourceNotFoundException("Задача не найдена");
                });
    }

    @Override
    @Transactional
    @CachePut(key = "#taskId")
    public Task updateTask(Long taskId, TaskUpdateRequest request) {
        var task = findTask(taskId);
        checkCurrentUserIsAuthor(task);

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setPriority(request.priority());

        return taskRepository.save(task);
    }

    @Override
    @CacheEvict(key = "#taskId")
    @Transactional
    public void deleteTask(Long taskId) {
        var task = findTask(taskId);
        checkCurrentUserIsAuthor(task);

        taskRepository.deleteById(taskId);
    }

    @Override
    @CachePut(key = "#taskId")
    @Transactional
    public Task assignTask(Long taskId, TaskAssigneeChangeRequest request) {
        var task = findTask(taskId);
        checkCurrentUserIsAuthor(task);

        var assigneeId = request.assigneeId();

        if (assigneeId == null) {
            task.setAssignee(null);
            return taskRepository.save(task);
        }

        var assignee = userAccountService.findUserById(assigneeId);
        task.setAssignee(assignee);
        return taskRepository.save(task);
    }

    @Override
    @CachePut(key = "#taskId")
    @Transactional
    public Task updateStatus(Long taskId, TaskStatusChangeRequest request) {
        var task = findTask(taskId);
        checkCurrentUserIsAuthorOrAssignee(task);

        task.setStatus(request.status());
        return taskRepository.save(task);
    }

    public void checkCurrentUserIsAuthor(Task task) {
        var currentUserId = userAccountService.getCurrentUser().getId();
        var taskAuthorId = task.getAuthor().getId();
        if (!currentUserId.equals(taskAuthorId)) {
            log.debug("Пользоветель id={} не является автором задачи id={}", currentUserId, task.getId());
            throw new AccessDeniedException("Доступ запрещен");
        }
    }

    public void checkCurrentUserIsAuthorOrAssignee(Task task) {
        var currentUserId = userAccountService.getCurrentUser().getId();
        var taskAuthorId = task.getAuthor().getId();
        var taskAssigneeId = task.getAssignee() == null ? null : task.getAssignee().getId();
        if (!currentUserId.equals(taskAuthorId) && !currentUserId.equals(taskAssigneeId)) {
            log.debug("Пользоветель id={} не является автором или исполнителем задачи id={}",
                    currentUserId, task.getId());
            throw new AccessDeniedException("Доступ запрещен");
        }
    }
}
