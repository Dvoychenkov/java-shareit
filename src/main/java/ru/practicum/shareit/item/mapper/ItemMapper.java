package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    Item toItem(NewItemDto newItemDto);

    ItemDto toItemDto(Item item);

    @Mapping(target = "comments", source = "comments")
    ItemDto toItemDto(Item item, Collection<Comment> comments);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItem(UpdateItemDto updateItemDto, @MappingTarget Item item);
}
