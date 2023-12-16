package io.github.alfabravo2013.taskmanagement.security.web;

import io.github.alfabravo2013.taskmanagement.security.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Аутентификация", description = "Методы для аутентификации пользователей")
public class AuthenticationRestController {
    private final AuthenticationService authenticationService;

    public AuthenticationRestController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "Логин по адресу email и паролю")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь аутентифицирован",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Пустой адрес email или пароль",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping(path = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<JwtResponse> login(
            @Parameter(description = "Тело запроса с адресом email и паролем пользователя")
            @Valid @RequestBody LoginRequest request
    ) {
        var token = authenticationService.getAccessToken(request);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
