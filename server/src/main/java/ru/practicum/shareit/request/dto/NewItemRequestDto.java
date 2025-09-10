package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class NewItemRequestDto {
    String description;

    @JsonCreator
    public NewItemRequestDto(@JsonProperty("description") String description) {
        this.description = description;
    }
}