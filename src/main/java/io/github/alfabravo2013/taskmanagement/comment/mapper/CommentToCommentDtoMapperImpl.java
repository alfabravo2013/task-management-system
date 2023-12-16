package io.github.alfabravo2013.taskmanagement.comment.mapper;

import io.github.alfabravo2013.taskmanagement.comment.model.Comment;
import io.github.alfabravo2013.taskmanagement.comment.web.CommentDto;

public class CommentToCommentDtoMapperImpl implements CommentToCommentDtoMapper {
    @Override
    public CommentDto map(Comment source) {
        return new CommentDto(
                source.getId(),
                source.getText(),
                source.getAuthor().getId(),
                source.getCreatedAt(),
                source.getTask().getId()
        );
    }
}
