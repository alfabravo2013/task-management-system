package io.github.alfabravo2013.taskmanagement.security.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AccessTokenServiceImplTest {
    @Autowired
    private AccessTokenService accessTokenService;

    @Test
    @DisplayName("if Principal is UserDetails, generate JWT")
    void generateTokenFromAuthentication() {
        UserDetails userDetails = User.withUsername("test1@test.com")
                .password("{noop}12345")
                .authorities("ROLE_USER")
                .build();
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails,null);

        var actual = accessTokenService.generateAccessToken(auth);

        assertThat(actual).isNotEmpty();
        assertThat(actual.split("\\.").length).isEqualTo(3);
    }

    @Test
    @DisplayName("throw RuntimeException if Principal is not UserDetails")
    void throwIfPrincipalIsNotUserDetails() {
        Authentication auth = new UsernamePasswordAuthenticationToken("test1@test.com",null);

        assertThatThrownBy(() -> accessTokenService.generateAccessToken(auth))
                .isInstanceOf(RuntimeException.class);
    }
}
