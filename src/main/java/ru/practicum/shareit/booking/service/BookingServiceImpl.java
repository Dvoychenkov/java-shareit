package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.shareit.utils.ValidationUtils.requireFound;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserService userService;
    private final ItemService itemService;

    private final BookingMapper bookingMapper;

    private static final String MSG_BOOKING_BY_ID_NOT_EXISTS = "Бронирование с ID %d не найдено";
    private static final String MSG_BOOKING_INTERVAL_INCORRECT = "Некорректный интервал бронирования: %s - %s";
    private static final String MSG_OWNER_CANT_BOOK_OWN_ITEM = "Владелец не может бронировать свою вещь";
    private static final String MSG_ITEM_NOT_AVAILABLE = "Вещь недоступна для бронирования";
    private static final String MSG_ONLY_ITEM_OWNER_CAN_MAKE_DECISION = "Только владелец вещи может принять решение";
    private static final String MSG_ONLY_CAN_CHANGE_STATUS_FROM_WAITING = "Изменение статуса возможно только из WAITING";
    private static final String MSG_ONLY_OWNER_OR_BOOKER_CAN_SEE_BROKING = "Бронирование доступно только владельцу или букеру";

    @Transactional
    @Override
    public BookingDto create(Long userId, NewBookingDto dto) {
        Item item = itemService.getItemOrThrow(dto.getItemId());
        User booker = userService.getUserOrThrow(userId);

        if (dto.getStart() == null || dto.getEnd() == null || !dto.getStart().isBefore(dto.getEnd())) {
            throw new IllegalArgumentException(
                    String.format(MSG_BOOKING_INTERVAL_INCORRECT, dto.getStart(), dto.getEnd()));
        }

        if (item.getOwner().equals(userId)) {
            throw new ForbiddenException(MSG_OWNER_CANT_BOOK_OWN_ITEM);
        }
        if (!item.isAvailable()) {
            throw new ValidationException(MSG_ITEM_NOT_AVAILABLE);
        }

        Booking booking = bookingMapper.toBooking(dto, item, booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(saved);
    }

    @Transactional
    @Override
    public BookingDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = getBookingOrThrow(bookingId);

        Item item = booking.getItem();
        if (!item.getOwner().equals(ownerId)) {
            throw new ForbiddenException(MSG_ONLY_ITEM_OWNER_CAN_MAKE_DECISION);
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException(MSG_ONLY_CAN_CHANGE_STATUS_FROM_WAITING);
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(saved);
    }

    @Override
    public BookingDto get(Long userId, Long bookingId) {
        userService.existsByIdOrThrow(userId);

        Booking booking = getBookingOrThrow(bookingId);

        Item item = booking.getItem();
        User booker = booking.getBooker();
        if (!booker.getId().equals(userId) && !item.getOwner().equals(userId)) {
            throw new ForbiddenException(MSG_ONLY_OWNER_OR_BOOKER_CAN_SEE_BROKING);
        }
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> getByBooker(Long userId, String stateRaw) {
        userService.existsByIdOrThrow(userId);

        BookingState state = BookingState.from(stateRaw);
        LocalDateTime now = LocalDateTime.now();

        Collection<Booking> result = switch (state) {
            case ALL -> bookingRepository.findByBooker_IdOrderByStartDesc(userId);
            case CURRENT ->
                    bookingRepository.findByBooker_IdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(userId, now, now);
            case PAST -> bookingRepository.findByBooker_IdAndEndLessThanOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository.findByBooker_IdAndStartGreaterThanOrderByStartDesc(userId, now);
            case WAITING -> bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        };
        return result.stream()
                .map(bookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public Collection<BookingDto> getByOwner(Long ownerId, String stateRaw) {
        userService.existsByIdOrThrow(ownerId);

        BookingState state = BookingState.from(stateRaw);
        LocalDateTime now = LocalDateTime.now();

        Collection<Booking> result = switch (state) {
            case ALL -> bookingRepository.findAllByOwner(ownerId);
            case CURRENT -> bookingRepository.findCurrentByOwner(ownerId, now);
            case PAST -> bookingRepository.findPastByOwner(ownerId, now);
            case FUTURE -> bookingRepository.findFutureByOwner(ownerId, now);
            case WAITING -> bookingRepository.findByOwnerAndStatus(ownerId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByOwnerAndStatus(ownerId, BookingStatus.REJECTED);
        };
        return result.stream()
                .map(bookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public Booking getBookingOrThrow(Long id) {
        return requireFound(bookingRepository.findById(id), () -> String.format(MSG_BOOKING_BY_ID_NOT_EXISTS, id));
    }
}
