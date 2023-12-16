package io.github.alfabravo2013.taskmanagement.account.repository;

import io.github.alfabravo2013.taskmanagement.account.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmailIgnoreCase(String email);
}
