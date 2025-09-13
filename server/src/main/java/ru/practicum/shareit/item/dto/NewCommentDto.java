package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class NewCommentDto {
    String text;

    @JsonCreator
    public NewCommentDto(@JsonProperty("text") String text) {
        this.text = text;
    }
}