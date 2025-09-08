package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validation.IdValid;

import java.util.Collection;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody NewBookingDto newBookingDto
    ) {
        return bookingService.create(userId, newBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @IdValid("bookingId") @PathVariable Long bookingId,
            @RequestParam("approved") boolean approved
    ) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long userId,
            @IdValid("bookingId") @PathVariable Long bookingId
    ) {
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> byBooker(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", defaultValue = "ALL") BookingState state
    ) {
        return bookingService.getByBooker(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> byOwner(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(value = "state", defaultValue = "ALL") BookingState state
    ) {
        return bookingService.getByOwner(ownerId, state);
    }
}
