package ru.practicum.shareit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.NewBookingDto;

public class BookingIntervalValidator implements ConstraintValidator<ValidBookingInterval, NewBookingDto> {

    @Override
    public boolean isValid(NewBookingDto dto, ConstraintValidatorContext ctx) {
        if (dto == null) {
            return true;
        }
        var start = dto.getStart();
        var end = dto.getEnd();

        if (start == null || end == null) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate("Укажите start и end")
                    .addConstraintViolation();
            return false;
        }
        if (!start.isBefore(end)) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate("start должно быть раньше end")
                    .addPropertyNode("start").addConstraintViolation();
            return false;
        }
        return true;
    }
}
