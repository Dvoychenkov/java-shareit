package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import ru.practicum.shareit.validation.ValidBookingInterval;

import java.time.LocalDateTime;

@Value
@ValidBookingInterval
public class NewBookingDto {
    @NotNull
    @FutureOrPresent
    LocalDateTime start;

    @NotNull
    @Future
    LocalDateTime end;

    @NotNull
    Long itemId;
}
