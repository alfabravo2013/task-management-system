package io.github.alfabravo2013.taskmanagement.task.mapper;

import io.github.alfabravo2013.taskmanagement.task.model.Task;
import io.github.alfabravo2013.taskmanagement.task.web.TaskDto;

public class TaskToTaskDtoMapperImpl implements TaskToTaskDtoMapper {
    @Override
    public TaskDto map(Task source) {
        return new TaskDto(
                source.getId(),
                source.getTitle(),
                source.getDescription(),
                source.getStatus(),
                source.getPriority(),
                source.getAuthor().getId(),
                source.getAssignee() == null ? null : source.getAssignee().getId()
        );
    }
}
