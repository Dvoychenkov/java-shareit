package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, NewBookingDto newBookingDto);

    BookingDto approve(Long ownerId, Long bookingId, boolean approved);

    BookingDto get(Long userId, Long bookingId);

    Collection<BookingDto> getByBooker(Long userId, BookingState state);

    Collection<BookingDto> getByOwner(Long ownerId, BookingState state);

    Booking getBookingOrThrow(Long id);
}
