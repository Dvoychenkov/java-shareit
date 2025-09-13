package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.validation.IdValid;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> createItem(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody NewItemDto newItemDto
    ) {
        log.info("createItem. newItemDto: {}", newItemDto);
        return client.createItem(userId, newItemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long userId,
            @IdValid("itemId") @PathVariable Long itemId,
            @Valid @RequestBody UpdateItemDto updateItemDto
    ) {
        log.info("updateItem. userId: {}, itemId: {}, updateItemDto: {}", userId, itemId, updateItemDto);
        return client.updateItem(userId, itemId, updateItemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long userId,
            @IdValid("itemId") @PathVariable Long itemId
    ) {
        log.info("getItem. userId: {}, itemId: {}", userId, itemId);
        return client.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsOfOwner(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long ownerId
    ) {
        log.info("getAllItemsOfOwner. ownerId: {}", ownerId);
        return client.getAllItemsOfOwner(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestParam("text") String text
    ) {
        log.info("searchItems. userId: {}, text: {}", userId, text);
        if (text == null || text.isBlank()) {
            return ResponseEntity.ok(List.of());
        }

        return client.searchItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long userId,
            @IdValid("itemId") @PathVariable Long itemId,
            @Valid @RequestBody NewCommentDto newCommentDto
    ) {
        log.info("addComment. userId: {}, itemId: {}, newCommentDto: {}", userId, itemId, newCommentDto);
        return client.addComment(userId, itemId, newCommentDto);
    }
}
