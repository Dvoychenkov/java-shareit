package ru.practicum.shareit.item.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
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
class ItemServiceIntegrationTest {

    private final EntityManager entityManager;
    private final ItemService itemService;
    private final UserRepository userRepository;

    private User user(String name) {
        User user = new User();
        user.setName(name);
        user.setEmail(name + "@mail.test");
        return userRepository.save(user);
    }

    private Item item(Long ownerId, String name, boolean available) {
        Item item = new Item();
        item.setName(name);
        item.setDescription("d");
        item.setAvailable(available);
        item.setOwner(ownerId);
        entityManager.persist(item);
        return item;
    }

    private Item item(Long ownerId, String name, String description, boolean available) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(ownerId);
        entityManager.persist(item);
        return item;
    }

    private Booking booking(Item item, User booker, LocalDateTime start, LocalDateTime end, BookingStatus bookingStatus) {
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
    void add_withRequestId() {
        // given
        User requester = user("requester");
        User owner = user("owner");

        // when
        LocalDateTime now = LocalDateTime.now();
        ItemRequest itemRequest = ItemRequest.builder()
                .description("Нужен шуруповёрт")
                .requestor(requester)
                .created(now)
                .build();
        entityManager.persist(itemRequest);

        ItemDto itemDto = itemService.add(
                new NewItemDto("Шуруповёрт", "Шуруповёртовый", true, itemRequest.getId()),
                owner.getId()
        );

        // then
        assertThat(itemDto.getId(), notNullValue());

        // when
        entityManager.flush();
        entityManager.clear();

        TypedQuery<Long> query = entityManager.createQuery("""
                select i.request
                from Item i
                where i.id = :id
                """, Long.class);
        Long itemRequestId = query.setParameter("id", itemDto.getId())
                .getSingleResult();

        // then
        assertThat(itemRequestId, equalTo(itemRequest.getId()));
    }

    @Test
    void findAllWithBookingsByOwnerId_populatesLastAndNextBookings() {
        // given
        User owner = user("owner");
        User booker = user("booker");
        Item item = item(owner.getId(), "I", true);

        LocalDateTime now = LocalDateTime.now();

        // when
        // past
        booking(item, booker, now.minusDays(3), now.minusDays(2), BookingStatus.APPROVED);

        List<ItemWithBookingsDto> itemsWithBookingsDtos = (List<ItemWithBookingsDto>) itemService
                .findAllWithBookingsByOwnerId(owner.getId());

        // then
        assertThat(itemsWithBookingsDtos, hasSize(1));
        ItemWithBookingsDto dto = itemsWithBookingsDtos.getFirst();
        assertThat(dto.getId(), equalTo(item.getId()));
        assertThat(dto.getLastBooking(), notNullValue());
        assertThat(dto.getNextBooking(), nullValue());
        assertThat(dto.getLastBooking().getStart(), lessThan(now));

        // when
        // future
        booking(item, booker, now.plusDays(1), now.plusDays(2), BookingStatus.APPROVED);
        itemsWithBookingsDtos = (List<ItemWithBookingsDto>) itemService.findAllWithBookingsByOwnerId(owner.getId());

        // then
        assertThat(itemsWithBookingsDtos, hasSize(1));
        dto = itemsWithBookingsDtos.getFirst();
        assertThat(dto.getId(), equalTo(item.getId()));
        assertThat(dto.getLastBooking(), notNullValue());
        assertThat(dto.getNextBooking(), notNullValue());
        assertThat(dto.getLastBooking().getStart(), lessThan(now));
        assertThat(dto.getNextBooking().getStart(), greaterThan(now));
        assertThat(dto.getNextBooking().getStart(), lessThan(now.plusDays(3)));

    }

    @Test
    void addComment_onlyAfterFinishedApprovedBooking() {
        // given
        User owner = user("owner");
        User booker = user("booker");
        Item item = item(owner.getId(), "I", true);
        LocalDateTime now = LocalDateTime.now();

        // future booking
        booking(item, booker, now.plusDays(1), now.plusDays(2), BookingStatus.APPROVED);

        // when/then
        assertThrows(ValidationException.class,
                () -> itemService.addComment(
                        item.getId(), booker.getId(), new NewCommentDto("Комментировать щё рано"))
        );

        // when
        // past booking
        booking(item, booker, now.minusDays(2), now.minusDays(1), BookingStatus.APPROVED);

        CommentDto created = itemService.addComment(
                item.getId(), booker.getId(), new NewCommentDto("А вот теперь норм"));

        TypedQuery<Comment> query = entityManager.createQuery("""
                select c
                from Comment c
                where c.id=:id
                """, Comment.class);
        Comment comment = query.setParameter("id", created.getId())
                .getSingleResult();

        // then
        assertThat(comment.getText(), is(created.getText()));
        assertThat(comment.getItem().getId(), is(item.getId()));
        assertThat(comment.getAuthor().getId(), is(booker.getId()));
    }

    @Test
    void searchAvailableItems_returnsListDtos() {
        // given
        User owner = user("owner");
        item(owner.getId(), "Дрель-9000", "мощная", true);
        item(owner.getId(), "Отвёртка", "для болтов", true);
        item(owner.getId(), "Молоток", "дрельная насадка", true);
        item(owner.getId(), "Сверло", "тупое", false);

        // when
        List<ItemDto> items = (List<ItemDto>) itemService.searchAvailableItems("ДРеЛь");

        // then
        assertThat(items, hasSize(2));
        assertThat(items, everyItem(hasProperty("available", is(true))));
        assertThat(items.stream()
                        .map(ItemDto::getName)
                        .toList(),
                containsInAnyOrder("Дрель-9000", "Молоток"));
    }

    @Test
    void searchAvailableItems_returnsEmpty() {
        // given
        User owner = user("owner");
        item(owner.getId(), "Что-то", "что-то", true);
        item(owner.getId(), "Что-то ещё", "что-то исчо", true);
        item(owner.getId(), "Что-то недоступное", "от слова совсем", false);

        // when
        List<ItemDto> result = (List<ItemDto>) itemService.searchAvailableItems("  ");

        // then
        assertThat(result, empty());

        TypedQuery<Long> query = entityManager.createQuery("""
                select count(i)
                from Item i
                """, Long.class);
        assertThat(query.getSingleResult(), greaterThan(0L));
    }

    @Test
    void save_updatesFields_whenOwner() {
        // given
        User owner = user("owner");
        Item item = item(owner.getId(), "старое", "старое описание", true);

        // when
        UpdateItemDto updateItemDto =  new UpdateItemDto("новое", "новое описание", false);
        ItemDto updated = itemService.save(item.getId(), updateItemDto, owner.getId());

        // when
        TypedQuery<Item> query = entityManager.createQuery("""
                select i
                from Item i
                where i.id=:id
                """, Item.class);
        Item persisted = query.setParameter("id", updated.getId())
                .getSingleResult();

        // then
        assertThat(persisted.getName(), is(updated.getName()));
        assertThat(persisted.getDescription(), is(updated.getDescription()));
        assertThat(persisted.isAvailable(), is(updated.isAvailable()));
    }

    @Test
    void save_throwsForbidden_whenNotOwner() {
        // given
        User owner = user("owner");
        User other = user("other");
        Item item = item(owner.getId(), "name", "desc", true);
        UpdateItemDto updateItemDto = new UpdateItemDto("x","y",true);

        // when/then
        assertThrows(ForbiddenException.class,
                () -> itemService.save(item.getId(), updateItemDto, other.getId()));
    }

    @Test
    void find_returnsItemWithComments_andNoBookings() {
        // given
        User owner = user("owner");
        User commenter = user("commenter");
        Item item = item(owner.getId(), "I", "D", true);

        Comment comment = new Comment(null, "Нормас", item, commenter, LocalDateTime.now());
        entityManager.persist(comment);

        // when
        ItemWithBookingsDto itemWithBookingsDto = itemService.find(item.getId());

        // then
        assertThat(itemWithBookingsDto.getId(), is(item.getId()));
        assertThat(itemWithBookingsDto.getComments(), hasSize(1));
        assertThat(itemWithBookingsDto.getComments().getFirst().getText(), is(comment.getText()));
        assertThat(itemWithBookingsDto.getLastBooking(), nullValue());
        assertThat(itemWithBookingsDto.getNextBooking(), nullValue());
    }
}
