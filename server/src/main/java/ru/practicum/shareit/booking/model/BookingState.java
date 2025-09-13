package ru.practicum.shareit.booking.model;

public enum BookingState {
    ALL,        // Все бронирования
    CURRENT,    // Текущие бронирования
    PAST,       // Завершённые бронирования
    FUTURE,     // Будущие бронирования
    WAITING,    // Новые бронирования, ожидают одобрения
    REJECTED;   // Бронирования отклонены владельцем
}
