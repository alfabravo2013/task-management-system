package io.github.alfabravo2013.taskmanagement.task.web;

import io.github.alfabravo2013.taskmanagement.common.validator.TaskPriorities;
import io.github.alfabravo2013.taskmanagement.common.validator.TaskStatuses;
import io.github.alfabravo2013.taskmanagement.task.model.TaskPriority;
import io.github.alfabravo2013.taskmanagement.task.model.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Schema(description = "Обновленное содержание задачи")
public record TaskUpdateRequest(
        @Schema(description = "Новый заголовок задачи до 256 символов", example = "Новая задача")
        @NotBlank(message = "Заголовок задачи не должен быть пустым")
        @Length(max = 256, message = "Максимальная длина заголовка 256 символов")
        String title,

        @Schema(description = "Новое текстовое описание задачи, не пустое", example = "Новый текст")
        @NotBlank(message = "Описание задачи не должно быть пустым")
        String description,

        @Schema(description = "Новый приоритет задачи", example = "HIGH")
        @TaskPriorities(message = "Приоритет задачи должен быть HIGH, MEDIUM или LOW")
        TaskPriority priority,

        @Schema(description = "Новый статус задачи", example = "WAITING")
        @TaskStatuses(message = "Статус задачи должен быть WAITING, IN_PROGRESS или COMPLETED")
        TaskStatus status
) {
}
