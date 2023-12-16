package io.github.alfabravo2013.taskmanagement.account.service;

import io.github.alfabravo2013.taskmanagement.account.model.UserAccount;
import io.github.alfabravo2013.taskmanagement.account.repository.UserAccountRepository;
import io.github.alfabravo2013.taskmanagement.account.web.UserRegisterRequest;
import io.github.alfabravo2013.taskmanagement.common.exception.EmailNotUniqueException;
import io.github.alfabravo2013.taskmanagement.common.exception.ResourceNotFoundException;
import io.github.alfabravo2013.taskmanagement.security.model.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
@Slf4j
public class UserAccountServiceImpl implements UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountServiceImpl(UserAccountRepository userAccountRepository,
                                  PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<UserAccount> findUserAccountByEmail(String email) {
        return userAccountRepository.findByEmailIgnoreCase(email);
    }

    @Override
    public void createUserAccount(UserRegisterRequest registrationRequest) {
        try {
            var userAccount = new UserAccount();
            userAccount.setEmail(registrationRequest.email().toLowerCase(Locale.ROOT));
            userAccount.setPassword(passwordEncoder.encode(registrationRequest.password()));
            userAccountRepository.save(userAccount);
        } catch (DataIntegrityViolationException e) {
            log.debug("Запрос '{}' нарушает целостность данных", registrationRequest);
            throw new EmailNotUniqueException("Этот адрес электронной почты уже зарегистрирован");
        }
    }

    @Override
    public UserAccount getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof SecurityUser securityUser) {
            return securityUser.getUserAccount();
        }
        if (principal instanceof Jwt jwt) {
            var subject = jwt.getSubject();
            return userAccountRepository.findByEmailIgnoreCase(subject)
                    .orElseThrow(() -> {
                        log.debug("subject токена '{}' не является известным адресом email", subject);
                        return new UsernameNotFoundException("Субъект JWT не найден");
                    });
        }
        if (principal instanceof UserDetails userDetails) {
            var username = userDetails.getUsername();
            return userAccountRepository.findByEmailIgnoreCase(username)
                    .orElseThrow(() -> {
                        log.debug("username '{}' в UserDetails не является известным адресом email", username);
                        return new UsernameNotFoundException("Пользователь не найден");
                    });
        }

        log.error("Принципал не является объектом UserDetails, User или Jwt! Обнаружен класс: {}",
                principal.getClass().getName());
        throw new RuntimeException("Ошибка аутентификации пользователя");
    }

    @Override
    public UserAccount findUserById(Long userAccountId) {
        return userAccountRepository.findById(userAccountId)
                .orElseThrow(() -> {
                    log.debug("Пользователь id={} не найден", userAccountId);
                    return new ResourceNotFoundException("Пользователь не найден");
                });
    }
}
