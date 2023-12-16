package io.github.alfabravo2013.taskmanagement.security.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Учетные данные пользователя")
public record LoginRequest(
        @Schema(description = "Адрес email", example = "test@test.com")
        @NotBlank(message = "Имя пользователя не должно быть пустым")
        String username,

        @Schema(description = "Пароль", example = "12345")
        @NotBlank(message = "Пароль не должен быть пустым")
        String password
) {
}
