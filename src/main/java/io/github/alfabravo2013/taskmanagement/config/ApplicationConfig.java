package io.github.alfabravo2013.taskmanagement.config;

import io.github.alfabravo2013.taskmanagement.comment.mapper.CommentToCommentDtoMapper;
import io.github.alfabravo2013.taskmanagement.comment.mapper.CommentToCommentDtoMapperImpl;
import io.github.alfabravo2013.taskmanagement.task.mapper.TaskToTaskDtoMapper;
import io.github.alfabravo2013.taskmanagement.task.mapper.TaskToTaskDtoMapperImpl;
import io.github.alfabravo2013.taskmanagement.task.mapper.TaskViewToTaskViewDtoMapper;
import io.github.alfabravo2013.taskmanagement.task.mapper.TaskViewToTaskViewDtoMapperImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public CommentToCommentDtoMapper commentToCommentDtoMapper() {
        return new CommentToCommentDtoMapperImpl();
    }

    @Bean
    public TaskViewToTaskViewDtoMapper taskViewToTaskViewDtoMapper() {
        return new TaskViewToTaskViewDtoMapperImpl();
    }

    @Bean
    public TaskToTaskDtoMapper taskToTaskDtoMapper() {
        return new TaskToTaskDtoMapperImpl();
    }
}
