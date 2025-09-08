package ru.practicum.shareit.booking.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class BookingTimeDto {
    LocalDateTime start;
    LocalDateTime end;
}
