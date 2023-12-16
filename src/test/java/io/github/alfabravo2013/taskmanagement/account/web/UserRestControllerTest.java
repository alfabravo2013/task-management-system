package io.github.alfabravo2013.taskmanagement.account.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
class UserRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/accounts/register returns 201 CREATED")
    void postRegistrationRequestReturnsStatusCode201() throws Exception {
        var email = UUID.randomUUID() + "@test.com";
        var requestBody = new UserRegisterRequest(email, "12345");
        var content = objectMapper.writeValueAsString(requestBody);

        var requestBuilder = MockMvcRequestBuilders
                .post("/api/v1/accounts/register")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.anonymous());
        mockMvc.perform(requestBuilder).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/v1/accounts/register returns 409 CONFLICT")
    void postRepeatedRegistrationRequestReturns409() throws Exception {
        var email = UUID.randomUUID() + "@test.com";
        var requestBody = new UserRegisterRequest(email, "12345");
        var content = objectMapper.writeValueAsString(requestBody);

        var requestBuilder = MockMvcRequestBuilders
                .post("/api/v1/accounts/register")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.anonymous());

        mockMvc.perform(requestBuilder).andExpect(status().isCreated());
        mockMvc.perform(requestBuilder).andExpect(status().isConflict());
    }

    @ParameterizedTest
    @MethodSource("registrationRequestFactory")
    @DisplayName("POST /api/v1/accounts/register returns 400 BAD REQUEST")
    void postInvalidRegistrationRequestReturns400(UserRegisterRequest request) throws Exception {
        var content = objectMapper.writeValueAsString(request);

        var requestBuilder = MockMvcRequestBuilders
                .post("/api/v1/accounts/register")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.anonymous());

        mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
    }

    static List<UserRegisterRequest> registrationRequestFactory() {
        return List.of(
                new UserRegisterRequest("test.test.com", "12345"),
                new UserRegisterRequest("", "12345"),
                new UserRegisterRequest("  ", "12345"),
                new UserRegisterRequest(null, "12345"),
                new UserRegisterRequest("test@test.com", "1234"),
                new UserRegisterRequest("test@test.com", "      "),
                new UserRegisterRequest("test@test.com", null)
        );
    }
}
