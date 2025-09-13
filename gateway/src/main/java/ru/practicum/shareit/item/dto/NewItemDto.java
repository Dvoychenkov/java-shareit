package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class NewItemDto {
    @NotBlank(message = "Пустое имя вещи")
    String name;

    @NotBlank(message = "Пустое описание вещи")
    String description;

    @NotNull(message = "Пустой признак доступности")
    Boolean available;

    Long requestId;
}
