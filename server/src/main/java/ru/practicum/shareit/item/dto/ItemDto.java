package ru.practicum.shareit.item.dto;

import lombok.Value;

import java.util.List;

@Value
public class ItemDto {
    Long id;
    String name;
    String description;
    boolean available;
    List<CommentDto> comments;
}
