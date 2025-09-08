package ru.practicum.shareit.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BookingIntervalValidator.class)
public @interface ValidBookingInterval {
    String message() default "Некорректный интервал бронирования: start должно быть раньше end";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
