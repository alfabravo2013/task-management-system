package io.github.alfabravo2013.taskmanagement.account.model;

import io.github.alfabravo2013.taskmanagement.comment.model.Comment;
import io.github.alfabravo2013.taskmanagement.task.model.Task;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Адрес электронной почты не должен быть пустым")
    @Email(message = "Неправильный формат адреса электронной почты")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Пароль не должен быть пустым")
    @Column(name = "password", nullable = false)
    private String password;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Task> tasks;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Comment> comments;
}
