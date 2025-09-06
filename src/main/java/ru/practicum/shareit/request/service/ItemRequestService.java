package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto add(Long requestorId, NewItemRequestDto dto);

    Collection<ItemRequestDto> getAllRequestsByRequestor(Long requestorId);

    Collection<ItemRequestDto> getAllRequestsExceptUser(Long userId);

    ItemRequestDto get(Long userId, Long requestId);

    ItemRequest getItemRequestOrThrow(Long id);
}