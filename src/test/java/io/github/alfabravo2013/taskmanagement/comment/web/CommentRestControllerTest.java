package io.github.alfabravo2013.taskmanagement.comment.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.alfabravo2013.taskmanagement.account.model.UserAccount;
import io.github.alfabravo2013.taskmanagement.account.repository.UserAccountRepository;
import io.github.alfabravo2013.taskmanagement.account.service.UserAccountService;
import io.github.alfabravo2013.taskmanagement.account.web.UserRegisterRequest;
import io.github.alfabravo2013.taskmanagement.comment.repository.CommentRepository;
import io.github.alfabravo2013.taskmanagement.task.model.Task;
import io.github.alfabravo2013.taskmanagement.task.model.TaskPriority;
import io.github.alfabravo2013.taskmanagement.task.model.TaskStatus;
import io.github.alfabravo2013.taskmanagement.task.repository.TaskRepository;
import junit.framework.AssertionFailedError;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
class CommentRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private CommentRepository commentRepository;

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

        var user = userAccountRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new AssertionFailedError("Пользователь '%s' не найден".formatted(email)));
        var task = new Task();
        task.setAuthor(user);
        task.setTitle("title");
        task.setDescription("description");
        task.setCreatedAt(LocalDateTime.now());
        task.setPriority(TaskPriority.LOW);
        task.setStatus(TaskStatus.WAITING);
        taskRepository.save(task);
    }

    @AfterEach
    void cleanUp() {
        commentRepository.deleteAll();
        taskRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/v1/tasks/<taskId>/comments returns 200 OK")
    void postNewComment() throws Exception {
        var taskId = taskRepository.findAll().get(0).getId();

        var content = objectMapper.writeValueAsString(new CommentCreateRequest("comment"));
        var request = post("/api/v1/tasks/" + taskId + "/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(jwtAuth);

        mockMvc.perform(request).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/v1/tasks/<taskId>/comments returns 400 BAD REQUEST")
    void postNewCommentInvalidBody() throws Exception {
        var content = objectMapper.writeValueAsString(new CommentCreateRequest(" "));
        var request = post("/api/v1/tasks/1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(jwt());

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/tasks/<invalid_taskId>/comments returns 404 NOT FOUND")
    void postNewCommentByInvalidTaskId() throws Exception {
        var content = objectMapper.writeValueAsString(new CommentCreateRequest("comment"));
        var request = post("/api/v1/tasks/10000/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(jwt());

        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/tasks/<taskId>/comments returns 200 OK and a page of comments")
    void getCommentPage() throws Exception {
        var taskId = taskRepository.findAll().get(0).getId();
        var userId = userAccountRepository.findByEmailIgnoreCase("test@test.com")
                .map(UserAccount::getId)
                .orElseThrow(() -> new AssertionFailedError("Пользователь 'test@test.com' не найден"));

        var content = objectMapper.writeValueAsString(new CommentCreateRequest("comment"));
        var postRequest = post("/api/v1/tasks/" + taskId + "/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(jwtAuth);

        mockMvc.perform(postRequest).andExpect(status().isCreated());

        var getRequest = get("/api/v1/tasks/" + taskId + "/comments").with(jwt());

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total_pages").value(1))
                .andExpect(jsonPath("$.current_page").value(1))
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.comments", hasSize(1)))
                .andExpect(jsonPath("$.comments[0].id").exists())
                .andExpect(jsonPath("$.comments[0].text").value("comment"))
                .andExpect(jsonPath("$.comments[0].task_id").value(taskId))
                .andExpect(jsonPath("$.comments[0].author_id").value(userId))
                .andExpect(jsonPath("$.comments[0].created_at").exists())
        ;
    }

    @Test
    @DisplayName("Endpoints return 401 UNAUTHORIZED for an unauthenticated user")
    void testEndpointSecurity() throws Exception {
        var request = post("/api/v1/tasks/1/comments");
        mockMvc.perform(request).andExpect(status().isUnauthorized());

        request = get("/api/v1/tasks/1/comments");
        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }
}
