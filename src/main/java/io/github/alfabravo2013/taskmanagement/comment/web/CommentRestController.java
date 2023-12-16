package io.github.alfabravo2013.taskmanagement.comment.web;

import io.github.alfabravo2013.taskmanagement.comment.mapper.CommentToCommentDtoMapper;
import io.github.alfabravo2013.taskmanagement.comment.service.CommentService;
import io.github.alfabravo2013.taskmanagement.common.validator.OneOf;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/tasks")
@SecurityRequirement(name = "bearer_auth")
@Tag(name = "Комментарии", description = "Методы для работы с комментариями")
@Validated
public class CommentRestController {
    private final CommentService commentService;
    private final CommentToCommentDtoMapper commentToCommentDtoMapper;

    public CommentRestController(CommentService commentService,
                                 CommentToCommentDtoMapper commentToCommentDtoMapper) {
        this.commentService = commentService;
        this.commentToCommentDtoMapper = commentToCommentDtoMapper;
    }

    @Operation(description = "Публикация нового комментария к задаче")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Комментарий опубликован"),
            @ApiResponse(responseCode = "400", description = "Пустой текст комментария",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Задача не найдена",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping(path = "/{taskId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public void createComment(
            @Parameter(description = "Уникальный идентификатор задачи")
            @PathVariable Long taskId,
            @Parameter(description = "Тело запроса с комментарием")
            @Valid @RequestBody CommentCreateRequest request
    ) {
        commentService.createComment(taskId, request);
    }

    @Operation(description = "Получения постраничного списка комментариев к задаче")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос выполнен",
                    content = @Content(schema = @Schema(implementation = CommentsPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Неверный номер или размер страницы",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Задача не найдена",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping(path = "/{taskId}/comments")
    public ResponseEntity<CommentsPageResponse> getTaskComments(
            @Parameter(description = "Уникальный идентификатор задачи")
            @PathVariable Long taskId,

            @Parameter(description = "Номер страницы")
            @Min(value = 1, message = "Номер страницы не может быть меньше 1")
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,

            @Parameter(description = "Размер страницы (10, 25 или 50")
            @OneOf(value = {10, 25, 50}, message = "Размер страницы должен быть 10, 25 или 50 элементов")
            @RequestParam(name = "perPage", required = false, defaultValue = "25") int perPage
    ) {
        var commentPage = commentService.findCommentsByTaskId(taskId, page - 1, perPage);
        var totalPages = commentPage.getTotalPages();
        var commentDTOs = commentPage.getContent().stream().map(commentToCommentDtoMapper::map).toList();
        var payload = new CommentsPageResponse(totalPages, page, commentDTOs);

        return ResponseEntity.ok().body(payload);
    }
}
