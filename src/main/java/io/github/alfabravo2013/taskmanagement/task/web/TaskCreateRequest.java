package io.github.alfabravo2013.taskmanagement.task.web;

import io.github.alfabravo2013.taskmanagement.common.validator.TaskPriorities;
import io.github.alfabravo2013.taskmanagement.task.model.TaskPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Schema(description = "Содержание новой задачи")
public record TaskCreateRequest(
        @Schema(description = "Заголовок задачи до 256 символов", example = "Задача №1")
        @NotBlank(message = "Заголовок задачи не должен быть пустым")
        @Length(max = 256, message = "Максимальная длина заголовка 256 символов")
        String title,

        @Schema(description = "Текстовое описание задачи, не пустое", example = "Выполнить задачу №1")
        @NotBlank(message = "Описание задачи не должно быть пустым")
        String description,

        @Schema(description = "Приоритет задачи", example = "HIGH")
        @TaskPriorities(message = "Приоритет задачи должен быть HIGH, MEDIUM или LOW")
        TaskPriority priority
) {
}
