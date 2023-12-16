package io.github.alfabravo2013.taskmanagement.comment.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Страница с комментариями к задаче")
public record CommentsPageResponse(
        @Schema(description = "Общее число страниц", example = "5")
        @JsonProperty("total_pages")
        long totalPages,

        @Schema(description = "Текущая страница", example = "1")
        @JsonProperty("current_page")
        int currentPage,

        @Schema(description = "Комментарии")
        List<CommentDto> comments
) {
}
