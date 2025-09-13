package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> createBooking(Long userId, NewBookingDto newBookingDto) {
        return post("", userId, newBookingDto);
    }

    public ResponseEntity<Object> approveBooking(Long ownerId, Long bookingId, boolean approved) {
        Map<String, Object> parameters = Map.of(
                "bookingId", bookingId,
                "approved", approved
        );

        return patch("/{bookingId}?approved={approved}", ownerId, parameters, null);
    }

    public ResponseEntity<Object> get(Long userId, Long bookingId) {
        Map<String, Object> parameters = Map.of(
                "bookingId", bookingId
        );

        return get("/{bookingId}", userId, parameters);
    }

    public ResponseEntity<Object> byBooker(Long userId, BookingState state) {
        Map<String, Object> parameters = Map.of(
                "state", state.name()
        );

        return get("?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> byOwner(Long ownerId, BookingState state) {
        Map<String, Object> parameters = Map.of(
                "state", state.name()
        );

        return get("/owner?state={state}", ownerId, parameters);
    }
}
