package io.github.alfabravo2013.taskmanagement.security.service;

import io.github.alfabravo2013.taskmanagement.security.web.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final AccessTokenService accessTokenService;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager,
                                     AccessTokenService accessTokenService) {
        this.authenticationManager = authenticationManager;
        this.accessTokenService = accessTokenService;
    }

    @Override
    public String getAccessToken(LoginRequest request) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                request.username(),
                request.password()
        );
        Authentication authenticated = authenticationManager.authenticate(authentication);
        return accessTokenService.generateAccessToken(authenticated);
    }
}
