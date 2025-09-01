package ru.practicum.shareit.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Optional;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationUtils {
    public static <T> T requireFound(Optional<T> optional, String notFoundMessage) {
        return optional.orElseThrow(() -> new NotFoundException(notFoundMessage));
    }

    public static <T> T requireFound(Optional<T> optional, Supplier<String> messageSupplier) {
        return optional.orElseThrow(() -> new NotFoundException(messageSupplier.get()));
    }

    public static void requireExists(boolean exists, String notFoundMessage) {
        if (!exists) {
            throw new NotFoundException(notFoundMessage);
        }
    }

    public static void requireExists(boolean exists, Supplier<String> messageSupplier) {
        if (!exists) {
            throw new NotFoundException(messageSupplier.get());
        }
    }
}