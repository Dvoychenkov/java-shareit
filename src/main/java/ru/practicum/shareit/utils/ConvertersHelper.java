package ru.practicum.shareit.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class ConvertersHelper {
    private ConvertersHelper() {
    }

    public static <T extends Enum<T>> String getEnumNamesFormatted(Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name).map(String::toLowerCase)
                .collect(Collectors.joining(", ", "[", "]"));
    }
}