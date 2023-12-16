package io.github.alfabravo2013.taskmanagement.security.service;

import io.github.alfabravo2013.taskmanagement.security.web.LoginRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private AccessTokenService accessTokenService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void generateToken() {
        var request = new LoginRequest("test@test.com", "12345");
        var token = "abc";
        Authentication auth = new UsernamePasswordAuthenticationToken("123", "123", List.of());

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(accessTokenService.generateAccessToken(auth)).thenReturn(token);

        var actual = authenticationService.getAccessToken(request);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(token);

        verify(authenticationManager, times(1)).authenticate(any());
        verify(accessTokenService, times(1)).generateAccessToken(auth);
    }

}
