package io.github.alfabravo2013.taskmanagement.task.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.alfabravo2013.taskmanagement.account.model.UserAccount;
import io.github.alfabravo2013.taskmanagement.account.repository.UserAccountRepository;
import io.github.alfabravo2013.taskmanagement.account.service.UserAccountService;
import io.github.alfabravo2013.taskmanagement.account.web.UserRegisterRequest;
import io.github.alfabravo2013.taskmanagement.task.model.Task;
import io.github.alfabravo2013.taskmanagement.task.model.TaskPriority;
import io.github.alfabravo2013.taskmanagement.task.model.TaskStatus;
import io.github.alfabravo2013.taskmanagement.task.repository.TaskRepository;
import junit.framework.AssertionFailedError;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class TaskRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private RequestPostProcessor jwtAuth;

    @BeforeEach
    void init() {
        var email = "test@test.com";
        userAccountService.createUserAccount(new UserRegisterRequest(email, "12345"));
        jwtAuth = jwt().jwt(builder -> builder.subject(email));
    }

    @AfterEach
    void cleanUp() {
        taskRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/v1/tasks returns 200 OK and a task page")
    void getEmptyTaskPage() throws Exception {
        var user = findUserByEmail("test@test.com");

        createTasks(user, 5);

        var requestBuilder = get("/api/v1/tasks").with(jwt());

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total_pages").value(1))
                .andExpect(jsonPath("$.current_page").value(1))
                .andExpect(jsonPath("$.tasks").isArray())
                .andExpect(jsonPath("$.tasks", hasSize(5)))
                .andExpect(jsonPath("$.tasks[0].id").exists())
                .andExpect(jsonPath("$.tasks[0].author_id").value(user.getId()))
                .andExpect(jsonPath("$.tasks[0].title").value("title"))
                .andExpect(jsonPath("$.tasks[0].description").value("description"))
                .andExpect(jsonPath("$.tasks[0].status").value("WAITING"))
                .andExpect(jsonPath("$.tasks[0].priority").value("LOW"))
                .andExpect(jsonPath("$.tasks[0].total_comments").value(0));
    }

    @Test
    @DisplayName("GET /api/v1/tasks?authorId=1 returns 200 OK and a task page")
    void getTaskPageByAuthorId() throws Exception {
        var user = findUserByEmail("test@test.com");

        createTasks(user, 4);

        var requestBuilder = get("/api/v1/tasks")
                .param("authorId", user.getId().toString())
                .with(jwtAuth);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total_pages").value(1))
                .andExpect(jsonPath("$.current_page").value(1))
                .andExpect(jsonPath("$.tasks").isArray())
                .andExpect(jsonPath("$.tasks", hasSize(4)));
    }

    @Test
    @DisplayName("GET /api/v1/tasks?assigneeId=1 returns 200 OK and a task page")
    void getTaskPageByAssigneeId() throws Exception {
        var user = findUserByEmail("test@test.com");

        userAccountService.createUserAccount(new UserRegisterRequest("test2@test.com", "12345"));
        var assignee = findUserByEmail("test2@test.com");

        createTasks(user, 3);

        var task = taskRepository.findAll().get(0);
        task.setAssignee(assignee);
        taskRepository.save(task);

        var requestBuilder = get("/api/v1/tasks")
                .param("assigneeId", assignee.getId().toString())
                .with(jwtAuth);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total_pages").value(1))
                .andExpect(jsonPath("$.current_page").value(1))
                .andExpect(jsonPath("$.tasks").isArray())
                .andExpect(jsonPath("$.tasks", hasSize(1)));
    }

    @ParameterizedTest
    @MethodSource("paramFactory")
    @DisplayName("GET /api/v1/tasks returns 400 BAD_REQUEST when request params are not valid")
    void getWithInvalidRequestParams(String page, String perPage) throws Exception {
        var requestBuilder = get("/api/v1/tasks")
                .param("page", page)
                .param("perPage", perPage)
                .with(jwt());

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/tasks returns 201 CREATED and a proper TaskDto")
    void postNewTask() throws Exception {
        var title = "task1";
        var description = "description";
        var priority = TaskPriority.HIGH;
        var status = TaskStatus.WAITING;

        var requestBody = new TaskCreateRequest(title, description, priority);
        var content = objectMapper.writeValueAsString(requestBody);
        var requestBuilder = post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(jwtAuth);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.priority").value(priority.name()))
                .andExpect(jsonPath("$.status").value(status.name()))
                .andExpect(jsonPath("$.author_id").exists());
    }

    @Test
    @DisplayName("GET /api/v1/tasks/<id> returns 200 and correct TaskView")
    void getTaskViewById() throws Exception {
        var user = findUserByEmail("test@test.com");

        createTasks(user, 1);

        var task = taskRepository.findAll().get(0);

        var request = get("/api/v1/tasks/" + task.getId()).with(jwt());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.author_id").value(user.getId()))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.priority").value("LOW"));
    }

    @ParameterizedTest
    @MethodSource("taskCreateRequestFactory")
    @DisplayName("POST /api/v1/tasks returns 400 BAD REQUEST if request body is invalid")
    void postInvalidNewTaskRequest(TaskCreateRequest createRequest) throws Exception {
        var content = objectMapper.writeValueAsString(createRequest);
        var requestBuilder = post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(jwtAuth);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/tasks/<id> returns 200 OK and updated TaskDto")
    void updateTask() throws Exception {
        var user = findUserByEmail("test@test.com");

        createTasks(user, 1);
        var task = taskRepository.findAll().get(0);

        var updateRequestBody = new TaskUpdateRequest(
                "new title",
                "new description",
                TaskPriority.HIGH,
                TaskStatus.IN_PROGRESS
        );
        var content = objectMapper.writeValueAsString(updateRequestBody);

        var request = put("/api/v1/tasks/" + task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(jwtAuth);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value("new title"))
                .andExpect(jsonPath("$.description").value("new description"))
                .andExpect(jsonPath("$.status").value(TaskStatus.IN_PROGRESS.name()))
                .andExpect(jsonPath("$.priority").value(TaskPriority.HIGH.name()));
    }

    @ParameterizedTest
    @MethodSource("taskUpdateRequestFactory")
    @DisplayName("PUT /api/v1/tasks/<id> returns 400 BAD REQUEST")
    void updateTaskWithInvalidContent(TaskUpdateRequest requestBody) throws Exception {
        var content = objectMapper.writeValueAsString(requestBody);
        var request = put("/api/v1/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(jwtAuth);

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/tasks/<id> returns 404 NOT FOUND")
    void updateNonExistingTask() throws Exception {
        var updateRequestBody = new TaskUpdateRequest(
                "new title",
                "new description",
                TaskPriority.HIGH,
                TaskStatus.IN_PROGRESS
        );
        var content = objectMapper.writeValueAsString(updateRequestBody);

        var request = put("/api/v1/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(jwtAuth);

        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/v1/tasks/<id> returns 403 FORBIDDEN")
    void updateTaskAccessDenied() throws Exception {
        userAccountService.createUserAccount(new UserRegisterRequest("test2@test.com", "12345"));
        var author = findUserByEmail("test2@test.com");

        createTasks(author, 1);

        var task = taskRepository.findAll().get(0);

        var updateRequestBody = new TaskUpdateRequest(
                "new title",
                "new description",
                TaskPriority.HIGH,
                TaskStatus.IN_PROGRESS
        );
        var content = objectMapper.writeValueAsString(updateRequestBody);

        var request = put("/api/v1/tasks/" + task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(jwtAuth);

        mockMvc.perform(request).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/v1/tasks/<id> returns 200 OK")
    void deleteTask() throws Exception {
        var user = findUserByEmail("test@test.com");
        createTasks(user, 1);

        var task = taskRepository.findAll().get(0);

        var deleteRequest = delete("/api/v1/tasks/" + task.getId())
                .with(jwtAuth);

        mockMvc.perform(deleteRequest).andExpect(status().isOk());

        assertThat(taskRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("DELETE /api/v1/tasks/<invalid_id> returns 404 NOT FOUND")
    void deleteNonExistingTask() throws Exception {
        var deleteRequest = delete("/api/v1/tasks/1")
                .with(jwtAuth);

        mockMvc.perform(deleteRequest).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/tasks/<id> returns 403 FORBIDDEN")
    void deleteTaskAccessDenied() throws Exception {
        userAccountService.createUserAccount(new UserRegisterRequest("test2@test.com", "12345"));
        var author = findUserByEmail("test2@test.com");

        createTasks(author, 1);

        var task = taskRepository.findAll().get(0);

        var deleteRequest = delete("/api/v1/tasks/" + task.getId())
                .with(jwtAuth);

        mockMvc.perform(deleteRequest).andExpect(status().isForbidden());

        assertThat(taskRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("PATCH /api/v1/tasks/<id>/assign returns 200 OK")
    void changeAssignee() throws Exception {
        userAccountService.createUserAccount(new UserRegisterRequest("test2@test.com", "12345"));
        var assignee = findUserByEmail("test2@test.com");

        var user = findUserByEmail("test@test.com");

        createTasks(user, 1);
        var task = taskRepository.findAll().get(0);

        var content = objectMapper.writeValueAsString(new TaskAssigneeChangeRequest(assignee.getId()));
        var request = patch("/api/v1/tasks/" + task.getId() + "/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(jwtAuth);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.assignee_id").value(assignee.getId()));
    }

    @Test
    @DisplayName("PATCH /api/v1/tasks/<id>/assignee returns 404 NOT FOUND")
    void setInvalidAssignee() throws Exception {
        var user = findUserByEmail("test@test.com");

        createTasks(user, 1);
        var taskId = taskRepository.findAll().get(0).getId();

        var content = objectMapper.writeValueAsString(new TaskAssigneeChangeRequest(10000L));
        var request = patch("/api/v1/tasks/" + taskId + "/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(jwtAuth);

        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/v1/tasks/<invalid_id>/assignee returns 404 NOT FOUND")
    void setAssigneeByInvalidTaskId() throws Exception {
        var content = objectMapper.writeValueAsString(new TaskAssigneeChangeRequest(10000L));
        var request = patch("/api/v1/tasks/" + 10000L + "/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(jwtAuth);

        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/v1/tasks/<id>/status returns 403 FORBIDDEN")
    void changeStatusAccessDenied() throws Exception {
        userAccountService.createUserAccount(new UserRegisterRequest("testA@test.com", "12345"));
        var author = findUserByEmail("testA@test.com");
        userAccountService.createUserAccount(new UserRegisterRequest("testB@test.com", "12345"));
        var assignee = findUserByEmail("testB@test.com");

        createTasks(author, 1);
        var task = taskRepository.findAll().get(0);
        task.setAssignee(assignee);
        taskRepository.save(task);

        var content = objectMapper.writeValueAsString(new TaskStatusChangeRequest(TaskStatus.WAITING));
        var request = patch("/api/v1/tasks/" + task.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(jwtAuth);

        mockMvc.perform(request).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH /api/v1/tasks/<id>/status returns 200 OK")
    void changeStatusByAuthor() throws Exception {
        var author = findUserByEmail("test@test.com");

        createTasks(author, 1);
        var task = taskRepository.findAll().get(0);

        var content = objectMapper.writeValueAsString(new TaskStatusChangeRequest(TaskStatus.WAITING));
        var request = patch("/api/v1/tasks/" + task.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(jwtAuth);

        mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/v1/tasks/<id>/status returns 200 OK")
    void changeStatusByAssignee() throws Exception {
        var assignee = findUserByEmail("test@test.com");

        userAccountService.createUserAccount(new UserRegisterRequest("testA@test.com", "12345"));
        var author = findUserByEmail("testA@test.com");

        createTasks(author, 1);
        var task = taskRepository.findAll().get(0);
        task.setAssignee(assignee);
        taskRepository.save(task);

        var content = objectMapper.writeValueAsString(new TaskStatusChangeRequest(TaskStatus.WAITING));
        var request = patch("/api/v1/tasks/" + task.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(jwtAuth);

        mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/v1/tasks/<invalid_id>/status returns 404 NOT FOUND")
    void setStatusByInvalidTaskId() throws Exception {
        var content = objectMapper.writeValueAsString(new TaskStatusChangeRequest(TaskStatus.WAITING));
        var request = patch("/api/v1/tasks/" + 10000L + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(jwtAuth);

        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Endpoints /api/v1/tasks/** return 401 UNAUTHORIZED")
    void accessEndpointsUnauthenticated() throws Exception {
        var requests = List.of(
                post("/api/v1/tasks"),
                get("/api/v1/tasks"),
                get("/api/v1/tasks/1"),
                patch("/api/v1/tasks/1/assignee"),
                patch("/api/v1/tasks/1/status"),
                put("/api/v1/tasks/1"),
                delete("/api/v1/tasks/1")
        );

        for (var request : requests) {
            mockMvc.perform(request).andExpect(status().isUnauthorized());
        }
    }

    private UserAccount findUserByEmail(String email) {
        return userAccountRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new AssertionFailedError("Пользователь '%s' не найден".formatted(email)));
    }

    private void createTasks(UserAccount author, int number) {
        for (int i = 0; i < number; i++) {
            var task = new Task();
            task.setAuthor(author);
            task.setTitle("title");
            task.setDescription("description");
            task.setCreatedAt(LocalDateTime.now());
            task.setPriority(TaskPriority.LOW);
            task.setStatus(TaskStatus.WAITING);
            taskRepository.save(task);
        }
    }

    private static List<TaskCreateRequest> taskCreateRequestFactory() {
        return List.of(
                new TaskCreateRequest(" ", "description", TaskPriority.HIGH),
                new TaskCreateRequest(null, "description", TaskPriority.HIGH),
                new TaskCreateRequest("title", " ", TaskPriority.HIGH),
                new TaskCreateRequest("title", null, TaskPriority.HIGH),
                new TaskCreateRequest("title", "description", null)
        );
    }

    private static List<TaskUpdateRequest> taskUpdateRequestFactory() {
        return List.of(
                new TaskUpdateRequest(" ", "description", TaskPriority.LOW, TaskStatus.WAITING),
                new TaskUpdateRequest(null, "description", TaskPriority.LOW, TaskStatus.WAITING),
                new TaskUpdateRequest("title", " ", TaskPriority.LOW, TaskStatus.WAITING),
                new TaskUpdateRequest("title", null, TaskPriority.LOW, TaskStatus.WAITING),
                new TaskUpdateRequest("title", "description", null, TaskStatus.WAITING),
                new TaskUpdateRequest("title", "description", TaskPriority.LOW, null)
        );
    }

    private static List<Arguments> paramFactory() {
        return List.of(
                Arguments.of("0", "25"),
                Arguments.of("1", "5")
        );
    }
}
