package io.github.alfabravo2013.taskmanagement.task.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.alfabravo2013.taskmanagement.task.model.TaskPriority;
import io.github.alfabravo2013.taskmanagement.task.model.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Информация о задаче")
public record TaskViewDto(
        @Schema(description = "Уникальный идентификатор задачи", example = "1")
        Long id,

        @Schema(description = "Заголовок задачи", example = "Задача №1")
        String title,

        @Schema(description = "Текст задачи", example = "Выполнить задачу №1")
        String description,

        @Schema(description = "Статус задачи", example = "WAITING")
        TaskStatus status,

        @Schema(description = "Приоритет задачи", example = "HIGH")
        TaskPriority priority,

        @Schema(description = "Уникальный идентификатор автора задачи", example = "1")
        @JsonProperty("author_id")
        Long authorId,

        @Schema(description = "Уникальный идентификатор исполнителя задачи", example = "1")
        @JsonProperty("assignee_id")
        Long assigneeId,

        @Schema(description = "Число комментариев к задаче", example = "2")
        @JsonProperty("total_comments")
        Integer totalComments
) {
}
