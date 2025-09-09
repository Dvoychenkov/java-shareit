package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.IdValid;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody NewItemDto newItemDto
    ) {
        return itemService.add(newItemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long userId,
            @IdValid("itemId") @PathVariable Long itemId,
            @Valid @RequestBody UpdateItemDto updateItemDto
    ) {
        return itemService.save(itemId, updateItemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto getItem(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long userId,
            @IdValid("itemId") @PathVariable Long itemId
    ) {
        return itemService.find(itemId);
    }

    @GetMapping
    public Collection<ItemWithBookingsDto> getAllItemsOfOwner(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long ownerId
    ) {
        return itemService.findAllWithBookingsByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestParam("text") String text
    ) {
        return itemService.searchAvailableItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long userId,
            @IdValid("itemId") @PathVariable Long itemId,
            @Valid @RequestBody NewCommentDto newCommentDto
    ) {
        return itemService.addComment(itemId, userId, newCommentDto);
    }
}
