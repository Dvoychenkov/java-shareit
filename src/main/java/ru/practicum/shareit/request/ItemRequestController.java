package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.validation.IdValid;

import java.util.Collection;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody NewItemRequestDto newItemRequestDto
    ) {
        return itemRequestService.add(userId, newItemRequestDto);
    }

    @GetMapping
    public Collection<ItemRequestDto> getAllItemsRequestsByRequestor(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long requestorId
    ) {
        return itemRequestService.getAllRequestsByRequestor(requestorId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAllItemsRequests(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemRequestService.getAllRequestsExceptUser(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long userId,
            @IdValid("requestId") @PathVariable Long requestId
    ) {
        return itemRequestService.get(userId, requestId);
    }
}