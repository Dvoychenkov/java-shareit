package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
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
    public ItemDto getItem(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long userId,
            @IdValid("itemId") @PathVariable Long itemId
    ) {
        return itemService.find(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAllItemsOfOwner(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemService.findAllByOwnerId(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestParam("text") String text
    ) {
        return itemService.searchAvailableItems(text);
    }
}
