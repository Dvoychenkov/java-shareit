package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestor", source = "requestor")
    @Mapping(target = "created", ignore = true)
    ItemRequest toItemRequest(NewItemRequestDto newItemRequestDto, User requestor);

    @Mapping(target = "items", expression = "java(java.util.List.of())")
    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    @Mapping(target = "items", source = "items")
    ItemRequestDto toItemRequestDto(ItemRequest itemRequest, Collection<Item> items);

    default ItemAnswerDto map(Item item) {
        if (item == null) {
            return null;
        }
        return new ItemAnswerDto(item.getId(), item.getName(), item.getOwner());
    }
}