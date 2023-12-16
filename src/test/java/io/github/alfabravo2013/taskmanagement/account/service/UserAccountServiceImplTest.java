package io.github.alfabravo2013.taskmanagement.account.service;

import io.github.alfabravo2013.taskmanagement.account.model.UserAccount;
import io.github.alfabravo2013.taskmanagement.account.repository.UserAccountRepository;
import io.github.alfabravo2013.taskmanagement.account.web.UserRegisterRequest;
import io.github.alfabravo2013.taskmanagement.common.exception.EmailNotUniqueException;
import io.github.alfabravo2013.taskmanagement.common.exception.ResourceNotFoundException;
import io.github.alfabravo2013.taskmanagement.security.model.SecurityUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceImplTest {
    @Mock
    private UserAccountRepository userAccountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserAccountServiceImpl userAccountService;

    @BeforeEach
    void init() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("When current user is SecurityUser return UserAccount")
    void testGetCurrentUserAccount() {
        var email = "test@test.com";
        var password = "12345";
        var user = new UserAccount();
        user.setEmail(email);
        user.setPassword(password);
        var securityUser = new SecurityUser(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(securityUser, password);
        SecurityContextHolder.getContext().setAuthentication(auth);

        var actual = userAccountService.getCurrentUser();

        assertThat(actual).isNotNull();
        assertThat(actual.getEmail()).isEqualTo(email);
        assertThat(actual.getPassword()).isEqualTo(password);

        verify(passwordEncoder, never()).encode(any());
        verify(userAccountRepository, never()).findByEmailIgnoreCase(any());
    }

    @Test
    @DisplayName("When current user is JWT token return UserAccount")
    void testGetCurrentUserFromJwt() {
        var tokenValue = "eyJraWQiOiJiYTFmZDBiZi1iN2U4LTRjMGQtOWQ5NC01OGUzODgzMGFjMTQiLCJhbGciOiJSUzI1NiJ9" +
                ".eyJzdWIiOiJ0ZXN0MUBnbWFpbC5jb20iLCJleHAiOjE3MDI3MjkzODQsImlhdCI6MTcwMjY0Mjk4NCwic2NvcGUi" +
                "OlsiUk9MRV9VU0VSIl19.0dlyqEIKGjcDRSOAuklsv093erKEgDvll3gU12VEKHM9DyUzjcDiQIAL0XeXyNhnLN-" +
                "FzYzhL28FqItMH5IEKjYu9Qodx1p-953fXDb3xU_qjcShyDoWXFeS1YwmAgx3jdTOD9RCXeVPQgplnAZN3p-" +
                "GZltUtqzUJzcDcoSm0sa_os33SzcOFTUWU5FnvGOmc4C3jaOfvnmCeV9RSilnFTzdPmr_05uYjxY1rhQwuj6VA7Ayf" +
                "-y5vlb00YQ3bAN3XC1hA49Gn4GYcuHM0fYsepipsscELlAMb2-JrvpsKctuMTY1zwUK-_rwyvc8VIdAHH-" +
                "sYB6hSqnytZ9hQgi0Ug";
        Jwt jwt = new Jwt(tokenValue, Instant.now(), Instant.now().plusSeconds(20),
                Map.of("abc", "abc"), Map.of("abc", "abc"));
        var user = new UserAccount();
        Authentication auth = new UsernamePasswordAuthenticationToken(jwt, null);

        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userAccountRepository.findByEmailIgnoreCase(any())).thenReturn(Optional.of(user));
        var actual = userAccountService.getCurrentUser();

        assertThat(actual).isNotNull();

        verify(passwordEncoder, never()).encode(any());
        verify(userAccountRepository, times(1)).findByEmailIgnoreCase(any());
    }

    @Test
    @DisplayName("When current user is UserDetails token return UserAccount")
    void testGetCurrentUserFromUserDetails() {
        var user = new UserAccount();
        var userDetails = User.withUsername("test@test.com").password("123").authorities("ROLE_USER").build();
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null);

        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userAccountRepository.findByEmailIgnoreCase(any())).thenReturn(Optional.of(user));
        var actual = userAccountService.getCurrentUser();

        assertThat(actual).isNotNull();

        verify(passwordEncoder, never()).encode(any());
        verify(userAccountRepository, times(1)).findByEmailIgnoreCase(any());
    }

    @Test
    @DisplayName("When JWT subject is not a valid email throw UsernameNotFound")
    void throwWhenInvalidJwtSubject() {
        var tokenValue = "eyJraWQiOiJiYTFmZDBiZi1iN2U4LTRjMGQtOWQ5NC01OGUzODgzMGFjMTQiLCJhbGciOiJSUzI1NiJ9" +
                ".eyJzdWIiOiJ0ZXN0MUBnbWFpbC5jb20iLCJleHAiOjE3MDI3MjkzODQsImlhdCI6MTcwMjY0Mjk4NCwic2NvcGUi" +
                "OlsiUk9MRV9VU0VSIl19.0dlyqEIKGjcDRSOAuklsv093erKEgDvll3gU12VEKHM9DyUzjcDiQIAL0XeXyNhnLN-" +
                "FzYzhL28FqItMH5IEKjYu9Qodx1p-953fXDb3xU_qjcShyDoWXFeS1YwmAgx3jdTOD9RCXeVPQgplnAZN3p-" +
                "GZltUtqzUJzcDcoSm0sa_os33SzcOFTUWU5FnvGOmc4C3jaOfvnmCeV9RSilnFTzdPmr_05uYjxY1rhQwuj6VA7Ayf" +
                "-y5vlb00YQ3bAN3XC1hA49Gn4GYcuHM0fYsepipsscELlAMb2-JrvpsKctuMTY1zwUK-_rwyvc8VIdAHH-" +
                "sYB6hSqnytZ9hQgi0Ug";
        Jwt jwt = new Jwt(tokenValue, Instant.now(), Instant.now().plusSeconds(20),
                Map.of("abc", "abc"), Map.of("abc", "abc"));
        Authentication auth = new UsernamePasswordAuthenticationToken(jwt, null);

        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userAccountRepository.findByEmailIgnoreCase(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userAccountService.getCurrentUser())
                .isInstanceOf(UsernameNotFoundException.class);

        verify(passwordEncoder, never()).encode(any());
        verify(userAccountRepository, times(1)).findByEmailIgnoreCase(any());
    }

    @Test
    @DisplayName("When UserDetail does not contain a valid username throw UsernameNotFound")
    void throwWhenInvalidUserDetailsUsername() {
        var userDetails = User.withUsername("test@test.com").password("123").authorities("ROLE_USER").build();
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null);

        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userAccountRepository.findByEmailIgnoreCase(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userAccountService.getCurrentUser())
                .isInstanceOf(UsernameNotFoundException.class);

        verify(passwordEncoder, never()).encode(any());
        verify(userAccountRepository, times(1)).findByEmailIgnoreCase(any());
    }

    @Test
    @DisplayName("When Principal is a not supported class throw RuntimeException")
    void throwWhenPrincipalIsUnsupportedClass() {
        var principal = "string";
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null);

        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThatThrownBy(() -> userAccountService.getCurrentUser())
                .isInstanceOf(RuntimeException.class);

        verify(passwordEncoder, never()).encode(any());
        verify(userAccountRepository, never()).findByEmailIgnoreCase(any());
    }

    @Test
    @DisplayName("When userId is valid return the UserAccount")
    void findUserById() {
        var userId = 1L;
        var user = new UserAccount();
        user.setId(userId);

        when(userAccountRepository.findById(userId)).thenReturn(Optional.of(user));

        var actual = userAccountService.findUserById(userId);

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(userId);

        verify(userAccountRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("When userId is not valid throw ResourceNotFound")
    void throwWhenUserAccountIdIsInvalid() {
        var userId = 1L;

        when(userAccountRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userAccountService.findUserById(userId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userAccountRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("When email is valid then return the Optional<UserAccount>")
    void findByEmail() {
        var email = "test@test.com";
        var user = new UserAccount();
        user.setEmail(email);

        when(userAccountRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(user));

        var actual = userAccountService.findUserAccountByEmail(email);

        assertThat(actual).isNotEmpty();
        assertThat(actual.map(UserAccount::getEmail).orElse(null)).isEqualTo(email);

        verify(userAccountRepository, times(1)).findByEmailIgnoreCase(email);
    }

    @Test
    @DisplayName("When email is not valid then return empty Optional")
    void whenEmailIsInvalidReturnEmptyOptional() {
        var email = "test@test.com";

        when(userAccountRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());

        var actual = userAccountService.findUserAccountByEmail(email);

        assertThat(actual).isEmpty();

        verify(userAccountRepository, times(1)).findByEmailIgnoreCase(email);
    }

    @Test
    @DisplayName("When email is unique run successfully")
    void createUserAccount() {
        var email = "test@test.com";
        var password = "12345";
        var request = new UserRegisterRequest(email, password);

        when(passwordEncoder.encode(password)).thenReturn(password);
        when(userAccountRepository.save(any())).thenReturn(new UserAccount());

        userAccountService.createUserAccount(request);

        verify(passwordEncoder, times(1)).encode(password);
        verify(userAccountRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("When email is not unique throw EmailNotUnique")
    void throwWhenEmailIsNotUnique() {
        var email = "test@test.com";
        var password = "12345";
        var request = new UserRegisterRequest(email, password);

        when(passwordEncoder.encode(password)).thenReturn(password);
        when(userAccountRepository.save(any())).thenThrow(new DataIntegrityViolationException("msg"));

        assertThatThrownBy(() -> userAccountService.createUserAccount(request))
                .isInstanceOf(EmailNotUniqueException.class);

        verify(passwordEncoder, times(1)).encode(password);
        verify(userAccountRepository, times(1)).save(any());
    }
}
