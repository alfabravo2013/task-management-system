package io.github.alfabravo2013.taskmanagement.security.service;

import org.springframework.security.core.Authentication;

public interface AccessTokenService {
    String generateAccessToken(Authentication authentication);
}
