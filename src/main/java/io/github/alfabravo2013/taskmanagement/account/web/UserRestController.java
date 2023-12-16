package io.github.alfabravo2013.taskmanagement.account.web;

import io.github.alfabravo2013.taskmanagement.account.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/accounts")
@Tag(name = "Пользователи", description = "Методы для работы с пользователями")
public class UserRestController {
    private final UserAccountService userAccountService;

    public UserRestController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Operation(summary = "Регистрация нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "409", description = "Адрес email уже был зарегистрирован",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Невалидный адрес email или пароль",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping(path = "/register", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(
            @Parameter(description = "Тело запроса с уникальным адресом email и паролем")
            @Valid @RequestBody UserRegisterRequest registrationRequest
    ) {
        userAccountService.createUserAccount(registrationRequest);
    }
}
