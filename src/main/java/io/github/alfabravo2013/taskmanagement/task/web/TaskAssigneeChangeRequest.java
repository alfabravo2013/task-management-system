package io.github.alfabravo2013.taskmanagement.task.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Идентификатор исполнителя задачи")
public record TaskAssigneeChangeRequest(
        @Schema(description = "Уникальный идентификатор пользователя", example = "1")
        @NotNull
        Long assigneeId
) { }
