package io.github.alfabravo2013.taskmanagement.security.service;

import io.github.alfabravo2013.taskmanagement.security.web.LoginRequest;

public interface AuthenticationService {
    String getAccessToken(LoginRequest request);
}
