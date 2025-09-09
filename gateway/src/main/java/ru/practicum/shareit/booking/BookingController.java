package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.validation.IdValid;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @IdValid("userId") @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody NewBookingDto newBookingDto
    ) {
        log.info("createBooking. userId: {}, newBookingDto: {}", userId, newBookingDto);
        return bookingClient.createBooking(userId, newBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @IdValid("userId") @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved
    ) {
        log.info("approveBooking. ownerId: {}, bookingId: {}, approved: {}", ownerId, bookingId, approved);
        return bookingClient.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId
    ) {
        log.info("getBooking. userId: {}, bookingId: {}", userId, bookingId);
        return bookingClient.get(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByBooker(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", defaultValue = "ALL") BookingState state
    ) {
        log.info("getBookingsByBooker. userId: {}, state: {}", userId, state);
        return bookingClient.byBooker(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(
            @IdValid("X-Sharer-User-Id") @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(value = "state", defaultValue = "ALL") BookingState state
    ) {
        log.info("getBookingsByOwner. ownerId: {}, state: {}", ownerId, state);
        return bookingClient.byOwner(ownerId, state);
    }
}
