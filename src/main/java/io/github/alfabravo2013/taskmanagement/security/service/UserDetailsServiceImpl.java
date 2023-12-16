package io.github.alfabravo2013.taskmanagement.security.service;

import io.github.alfabravo2013.taskmanagement.account.service.UserAccountService;
import io.github.alfabravo2013.taskmanagement.security.model.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserAccountService userAccountService;

    public UserDetailsServiceImpl(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userAccountService.findUserAccountByEmail(email)
                .map(SecurityUser::new)
                .orElseThrow(() -> {
                    log.debug("Пользователь не найден по адресу email: {}", email);
                    return new UsernameNotFoundException("Пользователь '%s' не найден".formatted(email));
                });
    }
}
