package ru.practicum.shareit.utils;

import ru.practicum.shareit.exception.NotFoundException;

import java.util.Optional;
import java.util.function.Supplier;

public class ValidationUtils {
    public static <T> T requireFound(Optional<T> optional, String notFoundMessage) {
        return optional.orElseThrow(() -> new NotFoundException(notFoundMessage));
    }

    public static <T> T requireFound(Optional<T> optional, Supplier<String> messageSupplier) {
        return optional.orElseThrow(() -> new NotFoundException(messageSupplier.get()));
    }
}