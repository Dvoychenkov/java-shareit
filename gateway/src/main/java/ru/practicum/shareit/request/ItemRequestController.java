package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.validation.IdValid;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient client;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody NewItemRequestDto newItemRequestDto
    ) {
        log.info("createItemRequest. userId: {}, newItemRequestDto: {}", userId, newItemRequestDto);
        return client.createItemRequest(userId, newItemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsRequestsByRequestor(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long requestorId
    ) {
        log.info("getAllItemsRequestsByRequestor. requestorId: {}", requestorId);
        return client.getAllItemsRequestsByRequestor(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemsRequests(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("getAllItemsRequests. userId: {}", userId);
        return client.getAllItemsRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long userId,
            @IdValid("requestId") @PathVariable Long requestId
    ) {
        log.info("getAllItemsRequests. userId: {}, requestId: {}", userId, requestId);
        return client.getItemRequest(userId, requestId);
    }
}
