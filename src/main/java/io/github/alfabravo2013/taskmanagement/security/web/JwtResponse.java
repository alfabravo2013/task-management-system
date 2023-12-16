package io.github.alfabravo2013.taskmanagement.security.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Тело ответа с токеном доступа к сервису")
public record JwtResponse(
        @Schema(description = "JWT")
        @JsonProperty("access_token")
        String accessToken
) {
}
