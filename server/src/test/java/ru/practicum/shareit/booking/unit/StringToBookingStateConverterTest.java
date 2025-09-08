package ru.practicum.shareit.booking.unit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.validation.StringToBookingStateConverter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StringToBookingStateConverterTest {

    private final StringToBookingStateConverter stringToBookingStateConverter = new StringToBookingStateConverter();

    @Test
    void convert_ok_ignoresCase() {
        // given
        String raw = "future";

        // when
        BookingState bookingState = stringToBookingStateConverter.convert(raw);

        // then
        assertThat(bookingState).isEqualTo(BookingState.FUTURE);
    }

    @Test
    void convert_blank_returnsAll() {
        // given
        String raw = "  ";

        // when
        BookingState bookingState = stringToBookingStateConverter.convert(raw);

        // then
        assertThat(bookingState).isEqualTo(BookingState.ALL);

        // given
        raw = null;

        // when
        bookingState = stringToBookingStateConverter.convert(raw);

        assertThat(bookingState).isEqualTo(BookingState.ALL);
    }

    @Test
    void convert_invalid_throwsValidationException() {
        // given
        String raw = "nope";

        // when/then
        assertThatThrownBy(() -> stringToBookingStateConverter.convert(raw))
                .isInstanceOf(ValidationException.class);
    }
}