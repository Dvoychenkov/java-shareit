package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody NewItemDto newItemDto
    ) {
        return itemService.add(newItemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody UpdateItemDto updateItemDto
    ) {
        return itemService.save(itemId, updateItemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto getItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId
    ) {
        return itemService.find(itemId);
    }

    @GetMapping
    public Collection<ItemWithBookingsDto> getAllItemsOfOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId
    ) {
        return itemService.findAllWithBookingsByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam("text") String text
    ) {
        return itemService.searchAvailableItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody NewCommentDto newCommentDto
    ) {
        return itemService.addComment(itemId, userId, newCommentDto);
    }
}
