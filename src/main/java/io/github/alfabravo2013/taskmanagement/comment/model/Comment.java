package io.github.alfabravo2013.taskmanagement.comment.model;

import io.github.alfabravo2013.taskmanagement.account.model.UserAccount;
import io.github.alfabravo2013.taskmanagement.task.model.Task;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Комментарий не должен быть пустым")
    @Column(name = "comment_text", nullable = false)
    private String text;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private UserAccount author;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
}
