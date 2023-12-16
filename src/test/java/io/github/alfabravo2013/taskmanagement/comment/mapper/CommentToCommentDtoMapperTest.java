package io.github.alfabravo2013.taskmanagement.comment.mapper;

import io.github.alfabravo2013.taskmanagement.account.model.UserAccount;
import io.github.alfabravo2013.taskmanagement.comment.model.Comment;
import io.github.alfabravo2013.taskmanagement.task.model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

class CommentToCommentDtoMapperTest {
    private final CommentToCommentDtoMapper mapper = new CommentToCommentDtoMapperImpl();

    @Test
    @DisplayName("a")
    void whenGetsCommentReturnsProperCommentDto() {
        var id = 1L;
        var taskId = 2L;
        var authorId = 3L;
        var text = "comment";
        var createdAt = LocalDateTime.now();

        var author = new UserAccount();
        author.setId(authorId);

        var task = new Task();
        task.setId(taskId);

        var comment = new Comment();
        comment.setId(id);
        comment.setText(text);
        comment.setTask(task);
        comment.setAuthor(author);
        comment.setCreatedAt(createdAt);

        var actual = mapper.map(comment);

        assertThat(actual.id()).isEqualTo(id);
        assertThat(actual.authorId()).isEqualTo(authorId);
        assertThat(actual.createdAt()).isEqualTo(createdAt);
        assertThat(actual.taskId()).isEqualTo(taskId);
        assertThat(actual.text()).isEqualTo(text);
    }

}
