package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "authorName", source = "comment.author.name")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "author", source = "authorComment")
    @Mapping(target = "item", source = "itemComment")
    @Mapping(target = "id", source = "commentDto.id")
    Comment toComment(CommentDto commentDto, User authorComment, Item itemComment);
}
