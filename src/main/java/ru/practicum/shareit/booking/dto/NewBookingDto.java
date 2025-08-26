package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class NewBookingDto {
    @NotNull
    LocalDateTime start;

    @NotNull
    LocalDateTime end;

    @NotNull
    Long itemId;
}
