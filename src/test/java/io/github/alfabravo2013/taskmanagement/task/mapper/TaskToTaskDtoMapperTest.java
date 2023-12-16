package io.github.alfabravo2013.taskmanagement.task.mapper;

import io.github.alfabravo2013.taskmanagement.account.model.UserAccount;
import io.github.alfabravo2013.taskmanagement.task.model.Task;
import io.github.alfabravo2013.taskmanagement.task.model.TaskPriority;
import io.github.alfabravo2013.taskmanagement.task.model.TaskStatus;
import io.github.alfabravo2013.taskmanagement.task.web.TaskDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaskToTaskDtoMapperTest {
    private final TaskToTaskDtoMapper mapper = new TaskToTaskDtoMapperImpl();

    private final Long id = 1L;
    private final String title = "title";
    private final TaskPriority priority = TaskPriority.LOW;
    private final TaskStatus status = TaskStatus.COMPLETED;
    private final String description = "description";
    private final Long authorId = 1L;

    @Test
    @DisplayName("Maps a Task to a proper TaskDto")
    void whenGetsEntityThenReturnsTaskDto() {
        var author = new UserAccount();
        author.setId(authorId);

        var assignee = new UserAccount();
        Long assigneeId = 2L;
        assignee.setId(assigneeId);

        var task = new Task();
        task.setId(id);
        task.setAuthor(author);
        task.setAssignee(assignee);
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setStatus(status);

        var actual = mapper.map(task);
        var expected = new TaskDto(id, title, description, status, priority, authorId, assigneeId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("When a Task has null Assignee, sets assigneeId in the TaskDto to null")
    void whenTaskHasNoAssigneeThenReturnsNullAssigneeId() {
        var author = new UserAccount();
        author.setId(authorId);

        var task = new Task();
        task.setId(id);
        task.setAuthor(author);
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setStatus(status);

        var actual = mapper.map(task);
        var expected = new TaskDto(id, title, description, status, priority, authorId, null);

        assertThat(actual).isEqualTo(expected);
    }
}
