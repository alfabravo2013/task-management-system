package io.github.alfabravo2013.taskmanagement.task.mapper;

import io.github.alfabravo2013.taskmanagement.task.model.TaskPriority;
import io.github.alfabravo2013.taskmanagement.task.model.TaskStatus;
import io.github.alfabravo2013.taskmanagement.task.model.TaskView;

public class MockTaskView implements TaskView {
    private final Long id;
    private final String title;
    private final String description;
    private final Long authorId;
    private final TaskStatus status;
    private final TaskPriority priority;
    private final Long assigneeId;
    private final int commentsCount;

    public MockTaskView(Long id, Long authorId, TaskStatus status, TaskPriority priority,
                        Long assigneeId, int commentsCount) {
        this.id = id;
        this.title = "title" + id;
        this.description = "description" + id;
        this.status = status;
        this.priority = priority;
        this.assigneeId = assigneeId;
        this.authorId = authorId;
        this.commentsCount = commentsCount;
    }
    @Override
    public Long getTaskId() {
        return id;
    }

    @Override
    public String getTaskTitle() {
        return title;
    }

    @Override
    public String getTaskDescription() {
        return description;
    }

    @Override
    public TaskStatus getTaskStatus() {
        return status;
    }

    @Override
    public TaskPriority getTaskPriority() {
        return priority;
    }

    @Override
    public Long getTaskAuthorId() {
        return authorId;
    }

    @Override
    public Long getTaskAssigneeId() {
        return assigneeId;
    }

    @Override
    public Integer getTaskCommentsCount() {
        return commentsCount;
    }
}
