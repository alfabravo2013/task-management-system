package io.github.alfabravo2013.taskmanagement.account.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Schema(description = "Регистрационные данные пользователя")
public record UserRegisterRequest(
        @Schema(description = "Уникальный адрес электронной почты", example = "test@test.com")
        @NotBlank(message = "Адрес электронной почты не должен быть пустым")
        @Email(message = "Неправильный формат адреса электронной почты")
        String email,

        @Schema(description = "Пароль длиной от 5 до 64 символов", example = "12345")
        @NotBlank(message = "Пароль не должен быть пустым")
        @Length(min = 5, max = 64, message = "Длина пароля должна быть от 5 до 64 символов")
        String password
) {
}
