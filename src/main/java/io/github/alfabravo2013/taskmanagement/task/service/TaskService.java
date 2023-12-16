package io.github.alfabravo2013.taskmanagement.task.service;

import io.github.alfabravo2013.taskmanagement.task.model.Task;
import io.github.alfabravo2013.taskmanagement.task.model.TaskView;
import io.github.alfabravo2013.taskmanagement.task.web.TaskAssigneeChangeRequest;
import io.github.alfabravo2013.taskmanagement.task.web.TaskCreateRequest;
import io.github.alfabravo2013.taskmanagement.task.web.TaskStatusChangeRequest;
import io.github.alfabravo2013.taskmanagement.task.web.TaskUpdateRequest;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface TaskService {
    Task createTask(TaskCreateRequest request);
    Task updateTask(Long taskId, TaskUpdateRequest request);
    void deleteTask(Long taskId);
    Task findTask(Long taskId);
    Page<TaskView> getTasks(Map<String, Long> filters, int page, int perPage);

    Task assignTask(Long taskId, TaskAssigneeChangeRequest request);

    Task updateStatus(Long taskId, TaskStatusChangeRequest request);
}
