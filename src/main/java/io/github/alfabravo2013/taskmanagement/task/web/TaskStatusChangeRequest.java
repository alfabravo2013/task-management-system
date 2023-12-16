package io.github.alfabravo2013.taskmanagement.task.web;

import io.github.alfabravo2013.taskmanagement.common.validator.TaskStatuses;
import io.github.alfabravo2013.taskmanagement.task.model.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Статус задачи")
public record TaskStatusChangeRequest(
        @Schema(description = "Новый статус задачи", example = "MEDIUM")
        @NotNull
        @TaskStatuses
        TaskStatus status
) {
}
