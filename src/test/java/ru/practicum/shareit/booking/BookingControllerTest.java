package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingBookerDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    BookingService bookingService;

    @Test
    void create_returnsDtoWithWaitingStatus() throws Exception {
        // given
        Long userId = 2L;
        LocalDateTime now = LocalDateTime.now();

        NewBookingDto newBookingDto = new NewBookingDto(
                now.plusDays(1),
                now.plusDays(2),
                100L
        );

        BookingDto bookingDto = new BookingDto(
                10L,
                newBookingDto.getStart(),
                newBookingDto.getEnd(),
                new BookingItemDto(newBookingDto.getItemId(), "ItemName"),
                new BookingBookerDto(userId),
                BookingStatus.WAITING
        );
        when(bookingService.create(userId, newBookingDto))
                .thenReturn(bookingDto);

        // when/then
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(newBookingDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.name().toUpperCase())));

        // verify
        verify(bookingService)
                .create(
                        eq(userId),
                        refEq(newBookingDto)
                );
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void approve_changesStatus() throws Exception {
        // given
        Long ownerId = 1L;
        Long bookingId = 10L;
        LocalDateTime now = LocalDateTime.now();

        BookingDto bookingDto = new BookingDto(
                bookingId,
                now,
                now.plusHours(1),
                new BookingItemDto(5L, "I"),
                new BookingBookerDto(2L),
                BookingStatus.APPROVED
        );
        when(bookingService.approve(ownerId, bookingId, true))
                .thenReturn(bookingDto);

        // when/then
        mockMvc.perform(patch("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingId), Long.class))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.name().toUpperCase())));

        // verify
        verify(bookingService).approve(ownerId, bookingId, true);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void get_returnsDto() throws Exception {
        // given
        Long userId = 1L;
        Long bookingId = 10L;
        LocalDateTime now = LocalDateTime.now();

        BookingDto bookingDto = new BookingDto(
                bookingId,
                now,
                now.plusHours(1),
                new BookingItemDto(5L, "I"),
                new BookingBookerDto(2L),
                BookingStatus.WAITING
        );
        when(bookingService.get(userId, bookingId)).thenReturn(bookingDto);

        // when/then
        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingId), Long.class));

        // verify
        verify(bookingService).get(userId, bookingId);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void byBooker_defaultStateAll_returnsDtoList() throws Exception {
        // given
        Long userId = 2L;
        when(bookingService.getByBooker(userId, BookingState.ALL)).thenReturn(List.of());

        // when/then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        // verify
        verify(bookingService).getByBooker(userId, BookingState.ALL);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void byOwner_stateWaiting_returnsDtoList() throws Exception {
        // given
        Long ownerId = 1L;
        when(bookingService.getByOwner(ownerId, BookingState.WAITING))
                .thenReturn(List.of());

        // when/then
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", BookingState.WAITING.name().toUpperCase()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        // verify
        verify(bookingService).getByOwner(ownerId, BookingState.WAITING);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void byBooker_invalidState_returns400() throws Exception {
        // given
        Long userId = 2L;

        // when / then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "NOT_EXISTING_STATE"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        // verify
        verifyNoInteractions(bookingService);
    }
}
