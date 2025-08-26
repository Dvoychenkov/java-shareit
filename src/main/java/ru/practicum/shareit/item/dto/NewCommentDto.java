package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class NewCommentDto {
    @NotBlank
    String text;

    @JsonCreator
    public NewCommentDto(@JsonProperty("text") String text) {
        this.text = text;
    }
}