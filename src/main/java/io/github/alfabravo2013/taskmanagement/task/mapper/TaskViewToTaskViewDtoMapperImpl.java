package io.github.alfabravo2013.taskmanagement.task.mapper;

import io.github.alfabravo2013.taskmanagement.task.model.TaskView;
import io.github.alfabravo2013.taskmanagement.task.web.TaskViewDto;

public class TaskViewToTaskViewDtoMapperImpl implements TaskViewToTaskViewDtoMapper {

    @Override
    public TaskViewDto map(TaskView source) {
        return new TaskViewDto(
                source.getTaskId(),
                source.getTaskTitle(),
                source.getTaskDescription(),
                source.getTaskStatus(),
                source.getTaskPriority(),
                source.getTaskAuthorId(),
                source.getTaskAssigneeId(),
                source.getTaskCommentsCount()
        );
    }
}
