package io.github.alfabravo2013.taskmanagement.task.model;

public interface TaskView {
    Long getTaskId();
    String getTaskTitle();
    String getTaskDescription();
    TaskStatus getTaskStatus();
    TaskPriority getTaskPriority();
    Long getTaskAuthorId();
    Long getTaskAssigneeId();
    Integer getTaskCommentsCount();
}
