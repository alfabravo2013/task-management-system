package io.github.alfabravo2013.taskmanagement.account.service;

import io.github.alfabravo2013.taskmanagement.account.model.UserAccount;
import io.github.alfabravo2013.taskmanagement.account.web.UserRegisterRequest;

import java.util.Optional;

public interface UserAccountService {
    Optional<UserAccount> findUserAccountByEmail(String email);
    void createUserAccount(UserRegisterRequest registrationRequest);

    UserAccount getCurrentUser();

    UserAccount findUserById(Long userAccountId);
}
