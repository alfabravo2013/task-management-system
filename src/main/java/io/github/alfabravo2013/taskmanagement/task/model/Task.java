package io.github.alfabravo2013.taskmanagement.task.model;

import io.github.alfabravo2013.taskmanagement.account.model.UserAccount;
import io.github.alfabravo2013.taskmanagement.comment.model.Comment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tasks")
@Getter
@Setter
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    @NotBlank(message = "Заголовок задачи не должен быть пустым")
    @Length(max = 256, message = "Максимальная длина заголовка 256 символов")
    private String title;

    @Column(name = "description", nullable = false)
    @NotBlank(message = "Описание задачи не должно быть пустым")
    private String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(name = "priority", nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private UserAccount author;

    @OneToOne
    @JoinColumn(name = "assignee_id")
    private UserAccount assignee;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<Comment> comments;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
