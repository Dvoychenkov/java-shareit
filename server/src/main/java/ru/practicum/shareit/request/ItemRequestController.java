package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody NewItemRequestDto newItemRequestDto
    ) {
        return itemRequestService.add(userId, newItemRequestDto);
    }

    @GetMapping
    public Collection<ItemRequestDto> getAllItemsRequestsByRequestor(
            @RequestHeader("X-Sharer-User-Id") Long requestorId
    ) {
        return itemRequestService.getAllRequestsByRequestor(requestorId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAllItemsRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemRequestService.getAllRequestsExceptUser(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId
    ) {
        return itemRequestService.get(userId, requestId);
    }
}