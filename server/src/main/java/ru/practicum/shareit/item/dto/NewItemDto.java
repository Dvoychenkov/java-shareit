package ru.practicum.shareit.item.dto;

import lombok.Value;

@Value
public class NewItemDto {
    String name;
    String description;
    Boolean available;
    Long requestId;
}
