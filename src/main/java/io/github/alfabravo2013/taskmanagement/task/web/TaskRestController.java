package io.github.alfabravo2013.taskmanagement.task.web;

import io.github.alfabravo2013.taskmanagement.common.validator.OneOf;
import io.github.alfabravo2013.taskmanagement.task.mapper.TaskToTaskDtoMapper;
import io.github.alfabravo2013.taskmanagement.task.mapper.TaskViewToTaskViewDtoMapper;
import io.github.alfabravo2013.taskmanagement.task.service.TaskService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/tasks")
@Slf4j
@SecurityRequirement(name = "bearer_auth")
@Tag(name = "Задачи", description = "Методы для работы с задачами")
@Validated
public class TaskRestController {
    private final TaskService taskService;
    private final TaskViewToTaskViewDtoMapper taskViewToTaskViewDtoMapper;
    private final TaskToTaskDtoMapper taskToTaskDtoMapper;

    public TaskRestController(TaskService taskService,
                              TaskViewToTaskViewDtoMapper taskViewToTaskViewDtoMapper,
                              TaskToTaskDtoMapper taskToTaskDtoMapper) {
        this.taskService = taskService;
        this.taskViewToTaskViewDtoMapper = taskViewToTaskViewDtoMapper;
        this.taskToTaskDtoMapper = taskToTaskDtoMapper;
    }

    @Operation(summary = "Получение постраничного списка задач")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос выполнен",
                    content = @Content(schema = @Schema(implementation = TaskPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Неверная страница или размер страницы",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping(produces = "application/json")
    public ResponseEntity<TaskPageResponse> getTasks(
            @Parameter(description = "Фильтр задач по идентификатору автора")
            @RequestParam(name = "authorId", required = false) Long authorId,

            @Parameter(description = "Фильтр задач по идентификатору исполнителя")
            @RequestParam(name = "assigneeId", required = false) Long assigneeId,

            @Parameter(description = "Номер страницы")
            @Min(value = 1, message = "Номер страницы не может быть меньше 1")
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,

            @Parameter(description = "Размер страницы (10, 25 или 50")
            @OneOf(value = {10, 25, 50}, message = "Размер страницы должен быть 10, 25 или 50 элементов")
            @RequestParam(name = "perPage", required = false, defaultValue = "25") int perPage
    ) {

        Map<String, Long> filters = new HashMap<>();
        if (authorId != null) {
            filters.put("authorId", authorId);
        }
        if (assigneeId != null) {
            filters.put("assigneeId", assigneeId);
        }

        var taskPage = taskService.getTasks(filters, page - 1, perPage);
        var totalPages = taskPage.getTotalPages();
        var taskDTOs = taskPage.getContent().stream().map(taskViewToTaskViewDtoMapper::map).toList();

        var payload = new TaskPageResponse(totalPages, page, taskDTOs);
        return ResponseEntity.ok().body(payload);
    }

    @Operation(summary = "Создание новой задачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Задача создана",
                    content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "400", description = "Запрос содержит невалидные данные",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<TaskDto> createTask(
            @Parameter(description = "Тело запроса с содержанием новой задачи")
            @Valid @RequestBody TaskCreateRequest request
    ) {
        var task = taskService.createTask(request);
        var taskDto = taskToTaskDtoMapper.map(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskDto);
    }

    @Operation(summary = "Поиск задачи по идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача найдена",
                    content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Задача не найдена",
                    content = @Content(schema = @Schema())),
    })
    @GetMapping(path = "/{taskId}", produces = "application/json")
    public ResponseEntity<TaskDto> getTask(
            @Parameter(description = "Уникальный идентификатор задачи")
            @PathVariable Long taskId
    ) {
        var task = taskService.findTask(taskId);
        var taskDto = taskToTaskDtoMapper.map(task);
        return ResponseEntity.ok().body(taskDto);
    }

    @Operation(summary = "Обновление задачи по идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача обновлена",
                    content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "400", description = "Запрос содержит невалидные данные",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "У пользователя нет прав на изменение задачи",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Задача не найдена",
                    content = @Content(schema = @Schema())),
    })
    @PutMapping(path = "/{taskId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TaskDto> updateTask(
            @Parameter(description = "Уникальный идентификатор задачи")
            @PathVariable Long taskId,
            @Parameter(description = "Тело запроса с новым содержанием задачи")
            @Valid @RequestBody TaskUpdateRequest request
    ) {
        var task = taskService.updateTask(taskId, request);
        var taskDto = taskToTaskDtoMapper.map(task);
        return ResponseEntity.ok().body(taskDto);
    }

    @Operation(summary = "Удаление задачи по идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Операция выполнена"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "У пользователя нет прав на удаление задачи",
                    content = @Content(schema = @Schema())),
    })
    @DeleteMapping(path = "/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTask(
            @Parameter(description = "Уникальный идентификатор задачи")
            @PathVariable Long taskId
    ) {
        taskService.deleteTask(taskId);
    }

    @Operation(summary = "Изменение исполнителя задачи по идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Исполнитель назначен",
                    content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "У пользователя нет прав на назначение исполнителя задачи",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Задача или исполнитель не найдены",
                    content = @Content(schema = @Schema())),
    })
    @PatchMapping(path = "/{taskId}/assign", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TaskDto> assignTask(
            @Parameter(description = "Уникальный идентификатор задачи")
            @PathVariable Long taskId,
            @Parameter(description = "Тело запроса с уникальным идентификатором исполнителя")
            @Valid @RequestBody TaskAssigneeChangeRequest request
    ) {
        var task = taskService.assignTask(taskId, request);
        var taskDto = taskToTaskDtoMapper.map(task);
        return ResponseEntity.ok().body(taskDto);
    }

    @Operation(summary = "Изменение статуса задачи по идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус изменен",
                    content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "400", description = "Невалидный новый статус задачи",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "У пользователя нет прав на изменение статуса задачи",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Задача не найдена",
                    content = @Content(schema = @Schema())),
    })
    @PatchMapping(path = "/{taskId}/status", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TaskDto> changeStatus(
            @Parameter(description = "Уникальный идентификатор задачи")
            @PathVariable Long taskId,
            @Parameter(description = "Новый статус запроса")
            @Valid @RequestBody TaskStatusChangeRequest request) {
        var task = taskService.updateStatus(taskId, request);
        var taskDto = taskToTaskDtoMapper.map(task);
        return ResponseEntity.ok().body(taskDto);
    }
}
