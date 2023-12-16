package io.github.alfabravo2013.taskmanagement.comment.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Комментарий к задаче")
public record CommentCreateRequest(
        @Schema(description = "Текст комментария", example = "Комментарий...")
        @NotBlank(message = "Комментарий не должен быть пустым")
        String text
) {
}
