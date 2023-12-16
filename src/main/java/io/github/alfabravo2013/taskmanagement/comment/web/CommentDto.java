package io.github.alfabravo2013.taskmanagement.comment.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Информация о комментарии")
public record CommentDto(
        @Schema(description = "Уникальный идентификатор комментария", example = "1")
        Long id,

        @Schema(description = "Текст комментария", example = "Комментарий...")
        String text,

        @Schema(description = "Уникальный идентификатор автора комментария", example = "1")
        @JsonProperty("author_id")
        Long authorId,

        @Schema(description = "Дата и время создания комментария", example = "2023-12-15T12:16:07.141061")
        @JsonProperty("created_at")
        LocalDateTime createdAt,

        @Schema(description = "Уникальный идентификатор задачи, к которой относится комментарий",
                example = "1")
        @JsonProperty("task_id")
        Long taskId
) {
}
