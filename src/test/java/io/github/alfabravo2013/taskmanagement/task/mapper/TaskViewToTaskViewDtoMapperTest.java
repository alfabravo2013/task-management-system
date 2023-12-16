package io.github.alfabravo2013.taskmanagement.task.mapper;

import io.github.alfabravo2013.taskmanagement.task.model.TaskPriority;
import io.github.alfabravo2013.taskmanagement.task.model.TaskStatus;
import io.github.alfabravo2013.taskmanagement.task.web.TaskViewDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaskViewToTaskViewDtoMapperTest {
    private final TaskViewToTaskViewDtoMapper mapper = new TaskViewToTaskViewDtoMapperImpl();

    final Long id = 1L;
    final String title = "title";
    final String description = "description";
    final TaskPriority priority = TaskPriority.LOW;
    final TaskStatus status = TaskStatus.COMPLETED;
    final Long authorId = 1L;
    final Long assigneeId = 2L;
    final int commentCount = 3;

    @Test
    @DisplayName("When gets a TaskView projection, returns a proper TaskViewDto")
    void whenGetsTaskViewReturnsProperTaskViewDto() {
        var taskView = new MockTaskView(id, authorId, status, priority, assigneeId, commentCount);

        var expected = new TaskViewDto(id, title + id, description + id, status, priority,
                authorId, assigneeId, commentCount);

        var actual = mapper.map(taskView);

        assertThat(actual).isEqualTo(expected);
    }
}
