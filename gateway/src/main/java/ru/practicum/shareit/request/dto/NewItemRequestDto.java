package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class NewItemRequestDto {
    @NotBlank(message = "Пустое описание запрашиваемой вещи")
    String description;

    @JsonCreator
    public NewItemRequestDto(@JsonProperty("description") String description) {
        this.description = description;
    }
}