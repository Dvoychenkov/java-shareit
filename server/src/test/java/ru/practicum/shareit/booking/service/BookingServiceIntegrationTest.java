package ru.practicum.shareit.booking.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class BookingServiceIntegrationTest {

    private final EntityManager entityManager;
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private User user(String name) {
        User user = new User();
        user.setName(name);
        user.setEmail(name + "@mail.test");
        return userRepository.save(user);
    }

    private Item item(Long ownerId, String name, boolean available) {
        Item item = new Item();
        item.setName(name);
        item.setDescription("Description");
        item.setAvailable(available);
        item.setOwner(ownerId);
        return itemRepository.save(item);
    }

    private Booking persist(Item item, User booker, LocalDateTime start, LocalDateTime end, BookingStatus bookingStatus) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(bookingStatus);
        entityManager.persist(booking);
        return booking;
    }

    @Test
    void create_persistsWaiting_withLinks() {
        // given
        User owner = user("owner");
        User booker = user("booker");
        Item item = item(owner.getId(), "I", true);

        LocalDateTime now = LocalDateTime.now();
        NewBookingDto newBookingDto = new NewBookingDto(now.plusDays(1), now.plusDays(2), item.getId());

        // when
        BookingDto created = bookingService.create(booker.getId(), newBookingDto);

        // then
        TypedQuery<Booking> query = entityManager.createQuery("""
                select b
                from Booking b
                where b.id=:id
                """, Booking.class);

        Booking booking = query.setParameter("id", created.getId()).getSingleResult();

        assertThat(booking.getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(booking.getItem().getId(), equalTo(item.getId()));
        assertThat(booking.getBooker().getId(), equalTo(booker.getId()));
    }

    @Test
    void approve_changesStatusToApproved() {
        // given
        User owner = user("owner");
        User booker = user("booker");
        Item item = item(owner.getId(), "I", true);

        LocalDateTime now = LocalDateTime.now();
        NewBookingDto newBookingDto = new NewBookingDto(now.plusDays(1), now.plusDays(2), item.getId());

        BookingDto created = bookingService.create(booker.getId(), newBookingDto);

        // when
        BookingDto approved = bookingService.approve(owner.getId(), created.getId(), true);

        // then
        assertThat(approved.getStatus(), equalTo(BookingStatus.APPROVED));
        Booking booking = entityManager.find(Booking.class, approved.getId());
        assertThat(booking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void byBooker_filtersByState_sortedDesc() {
        // given
        User owner = user("owner");
        User booker = user("booker");
        Item item = item(owner.getId(), "I", true);
        LocalDateTime now = LocalDateTime.now();

        // PAST
        persist(item, booker, now.minusDays(3), now.minusDays(2), BookingStatus.APPROVED);
        // CURRENT
        persist(item, booker, now.minusHours(1), now.plusHours(1), BookingStatus.APPROVED);
        // FUTURE
        persist(item, booker, now.plusDays(1), now.plusDays(2), BookingStatus.APPROVED);
        // WAITING
        persist(item, booker, now.plusDays(3), now.plusDays(4), BookingStatus.WAITING);
        // REJECTED
        persist(item, booker, now.plusDays(5), now.plusDays(6), BookingStatus.REJECTED);

        // when/then
        List<BookingDto> all = (List<BookingDto>) bookingService.getByBooker(booker.getId(), BookingState.ALL);
        assertThat(all, hasSize(5));
        assertThat(all.getFirst().getStart(), greaterThan(all.get(1).getStart()));

        List<BookingDto> past = (List<BookingDto>) bookingService.getByBooker(booker.getId(), BookingState.PAST);
        assertThat(past, everyItem(hasProperty("end", lessThan(now))));

        List<BookingDto> future = (List<BookingDto>) bookingService.getByBooker(booker.getId(), BookingState.FUTURE);
        assertThat(future, everyItem(hasProperty("start", greaterThan(now))));

        List<BookingDto> current = (List<BookingDto>) bookingService.getByBooker(booker.getId(), BookingState.CURRENT);
        assertThat(current, hasSize(1));
        assertThat(current.getFirst().getStart(), lessThanOrEqualTo(now));
        assertThat(current.getFirst().getEnd(), greaterThanOrEqualTo(now));

        List<BookingDto> waiting = (List<BookingDto>) bookingService.getByBooker(booker.getId(), BookingState.WAITING);
        assertThat(waiting, everyItem(hasProperty("status", is(BookingStatus.WAITING))));

        List<BookingDto> rejected = (List<BookingDto>) bookingService.getByBooker(booker.getId(), BookingState.REJECTED);
        assertThat(rejected, everyItem(hasProperty("status", is(BookingStatus.REJECTED))));
    }

    @Test
    void byOwner_filtersByState() {
        // given
        User owner = user("owner");
        User booker = user("booker");
        Item item = item(owner.getId(), "I", true);
        LocalDateTime now = LocalDateTime.now();

        // PAST
        persist(item, booker, now.minusDays(2), now.minusDays(1), BookingStatus.APPROVED);
        // FUTURE
        persist(item, booker, now.plusDays(1), now.plusDays(2), BookingStatus.APPROVED);
        // WAITING
        persist(item, booker, now.plusDays(3), now.plusDays(4), BookingStatus.WAITING);

        // when/then
        List<BookingDto> all = (List<BookingDto>) bookingService.getByOwner(owner.getId(), BookingState.ALL);
        assertThat(all, hasSize(3));

        List<BookingDto> future = (List<BookingDto>) bookingService.getByOwner(owner.getId(), BookingState.FUTURE);
        assertThat(future, hasSize(2));

        List<BookingDto> waiting = (List<BookingDto>) bookingService.getByOwner(owner.getId(), BookingState.WAITING);
        assertThat(waiting, hasSize(1));
        assertThat(waiting.getFirst().getStatus(), is(BookingStatus.WAITING));
    }

    @Test
    void get_allowedForBookerAndOwner_forbiddenForOthers() {
        // given
        LocalDateTime now = LocalDateTime.now();

        User owner = user("owner");
        User booker = user("booker");
        User stranger = user("stranger");
        Item item = item(owner.getId(), "I", true);

        NewBookingDto newBookingDto = new NewBookingDto(now.plusDays(1), now.plusDays(2), item.getId());
        BookingDto created = bookingService.create(booker.getId(), newBookingDto);

        // when
        BookingDto byOwner  = bookingService.get(owner.getId(), created.getId());
        BookingDto byBooker = bookingService.get(booker.getId(), created.getId());

        // then
        assertThat(byOwner.getId(), is(created.getId()));
        assertThat(byBooker.getId(), is(created.getId()));

        assertThrows(ForbiddenException.class,
                () -> bookingService.get(stranger.getId(), created.getId()));
    }
}
