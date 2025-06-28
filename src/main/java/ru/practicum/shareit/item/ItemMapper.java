package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {
    public static Item toItem(NewItemDto newItemDto) {
        Item item = new Item();
        item.setName(newItemDto.getName());
        item.setDescription(newItemDto.getDescription());
        item.setAvailable(newItemDto.getAvailable());
        return item;
    }

    public static void updateItem(Item item, UpdateItemDto updateItemDto) {
        if (updateItemDto.getName() != null && !updateItemDto.getName().isBlank()) {
            item.setName(updateItemDto.getName());
        }
        if (updateItemDto.getDescription() != null && !updateItemDto.getDescription().isBlank()) {
            item.setDescription(updateItemDto.getDescription());
        }
        if (updateItemDto.getAvailable() != null) {
            item.setAvailable(updateItemDto.getAvailable());
        }
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable()
        );
    }
}
