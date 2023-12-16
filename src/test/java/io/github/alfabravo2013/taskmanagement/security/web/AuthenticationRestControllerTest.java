package io.github.alfabravo2013.taskmanagement.security.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.alfabravo2013.taskmanagement.account.repository.UserAccountRepository;
import io.github.alfabravo2013.taskmanagement.account.service.UserAccountService;
import io.github.alfabravo2013.taskmanagement.account.web.UserRegisterRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @AfterEach
    void cleanUp() {
        userAccountRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/v1/auth/login returns 401 UNAUTHORIZED")
    void registerWithInvalidCredentials() throws Exception {
        var content = objectMapper.writeValueAsString(new LoginRequest("abc", "123"));
        var request = MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login returns 200 OK and an access token")
    void registerWithEmailAlreadyTaken() throws Exception {
        userAccountService.createUserAccount(new UserRegisterRequest("test@test.com", "12345"));

        var content = objectMapper.writeValueAsString(new LoginRequest("test@test.com", "12345"));
        var request = MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.access_token").isString())
        ;
    }
}
