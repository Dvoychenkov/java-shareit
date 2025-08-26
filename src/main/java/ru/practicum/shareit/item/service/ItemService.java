package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    ItemDto add(NewItemDto newItemDto, Long ownerId);

    ItemDto save(Long id, UpdateItemDto updateItemDto, Long ownerId);

    ItemWithBookingsDto find(Long id);

    Collection<ItemWithBookingsDto> findAllWithBookingsByOwnerId(Long ownerId);

    Collection<ItemDto> searchAvailableItems(String searchText);

    CommentDto addComment(Long itemId, Long userId, NewCommentDto dto);

    Item getItemOrThrow(Long id);
}
