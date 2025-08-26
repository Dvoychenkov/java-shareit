package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.booking.dto.BookingTimeDto;

@Value
public class ItemWithBookingsDto {
    Long id;
    String name;
    String description;
    boolean available;
    BookingTimeDto lastBooking;
    BookingTimeDto nextBooking;
}