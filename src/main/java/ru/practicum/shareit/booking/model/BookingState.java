package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.ValidationException;

public enum BookingState {
    ALL,        // Все бронирования
    CURRENT,    // Текущие бронирования
    PAST,       // Завершённые бронирования
    FUTURE,     // Будущие бронирования
    WAITING,    // Новые бронирования, ожидают одобрения
    REJECTED;   // Бронирования отклонены владельцем

    public static BookingState from(String bookingState) {
        if (bookingState == null || bookingState.isBlank()) {
            return ALL;
        }

        try {
            return BookingState.valueOf(bookingState.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Некорректный запрашиваемый статус: " + bookingState);
        }
    }
}
