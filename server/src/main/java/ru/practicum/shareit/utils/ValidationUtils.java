package ru.practicum.shareit.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Optional;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationUtils {

    public static <T> T requireFound(Optional<T> optional, Supplier<String> messageSupplier) {
        return optional.orElseThrow(() -> new NotFoundException(messageSupplier.get()));
    }

    public static void requireExists(boolean exists, Supplier<String> messageSupplier) {
        if (!exists) {
            throw new NotFoundException(messageSupplier.get());
        }
    }
}