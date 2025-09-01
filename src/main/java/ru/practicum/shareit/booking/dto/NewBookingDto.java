package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import ru.practicum.shareit.validation.ValidBookingInterval;

import java.time.LocalDateTime;

@Value
@ValidBookingInterval
public class NewBookingDto {
    @NotNull
    LocalDateTime start;

    @NotNull
    LocalDateTime end;

    @NotNull
    Long itemId;
}
