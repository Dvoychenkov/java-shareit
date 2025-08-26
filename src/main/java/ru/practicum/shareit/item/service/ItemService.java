package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    ItemDto add(NewItemDto newItemDto, Long ownerId);

    ItemDto save(Long id, UpdateItemDto updateItemDto, Long ownerId);

    ItemDto find(Long id);

    Collection<ItemWithBookingsDto> findAllWithBookingsByOwnerId(Long ownerId);

    Collection<ItemDto> searchAvailableItems(String searchText);

    Item getItemOrThrow(Long id);
}
