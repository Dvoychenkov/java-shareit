package ru.practicum.shareit.validation;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.utils.ConvertersHelper;

@Component
public class StringToBookingStateConverter implements Converter<String, BookingState> {
    @Override
    public BookingState convert(String source) {
        if (source == null || source.isBlank()) return BookingState.ALL;
        try {
            return BookingState.valueOf(source.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ValidationException(
                    "Недопустимое значение параметра 'state': " + source
                            + ". Ожидаются значения из списка: "
                            + ConvertersHelper.getEnumNamesFormatted(BookingState.class)
            );
        }
    }
}