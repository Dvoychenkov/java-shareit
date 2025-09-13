package ru.practicum.shareit.request.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemRequestServiceIntegrationTest {

    private final EntityManager entityManager;
    private final ItemRequestService itemRequestService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private User user(String name) {
        User user = new User();
        user.setName(name);
        user.setEmail(name + "@mail.test");
        return userRepository.save(user);
    }

    @Test
    void add() {
        // given
        User alice = user("alice");

        ItemRequestDto itemRequestDto = itemRequestService.add(alice.getId(),
                new NewItemRequestDto("Нужен перфоратор"));

        // when
        TypedQuery<ItemRequest> query = entityManager.createQuery("""
                select ir
                from ItemRequest ir
                where ir.id = :id
                """, ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id", itemRequestDto.getId())
                .getSingleResult();

        // then
        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequest.getRequestor().getId(), equalTo(alice.getId()));
        assertThat(itemRequest.getCreated(), notNullValue());
    }

    @Test
    void get() {
        // given
        User requester = user("requester");
        User owner = user("owner");

        ItemRequestDto itemRequestDto = itemRequestService.add(requester.getId(),
                new NewItemRequestDto("Нужна дрель"));

        Item i1 = new Item();
        i1.setName("Дрель-1");
        i1.setDescription("Дрель 80 lvl");
        i1.setAvailable(true);
        i1.setOwner(owner.getId());
        i1.setRequest(itemRequestDto.getId());
        itemRepository.save(i1);

        Item i2 = new Item();
        i2.setName("Дрель-2");
        i2.setDescription("Дрель 100500 lvl");
        i2.setAvailable(true);
        i2.setOwner(owner.getId());
        i2.setRequest(itemRequestDto.getId());
        itemRepository.save(i2);

        // when
        TypedQuery<Item> query = entityManager.createQuery("""
                select i from
                Item i
                where i.request = :rid
                order by i.name
                """, Item.class);
        List<Item> answerItems = query.setParameter("rid", itemRequestDto.getId())
                .getResultList();

        // then
        assertThat(answerItems, hasSize(2));
        assertThat(answerItems, everyItem(hasProperty("owner", equalTo(owner.getId()))));

        // when
        ItemRequestDto answerItemsDtos = itemRequestService.get(owner.getId(), itemRequestDto.getId());

        // then
        assertThat(answerItemsDtos.getItems(), hasSize(2));
        assertThat(answerItemsDtos.getItems().getFirst().getOwnerId(), equalTo(owner.getId()));
    }

    @Test
    void getAllRequestsByRequestor_getAllRequestsExceptUser() {
        // given
        User alice = user("alice");
        User bob = user("bob");
        LocalDateTime now = LocalDateTime.now();

        // when
        ItemRequest ir1 = ItemRequest.builder()
                .description("Дрель простая")
                .requestor(alice)
                .created(now.minusHours(2))
                .build();
        entityManager.persist(ir1);

        ItemRequest ir2 = ItemRequest.builder()
                .description("Дрель средняя")
                .requestor(alice)
                .created(now)
                .build();
        entityManager.persist(ir2);

        ItemRequest ir3 = ItemRequest.builder()
                .description("Дрель мощная")
                .requestor(bob)
                .created(now.minusHours(1))
                .build();
        entityManager.persist(ir3);

        List<ItemRequestDto> getAllRequestsByRequestor = (List<ItemRequestDto>) itemRequestService
                .getAllRequestsByRequestor(alice.getId());

        // then
        assertThat(getAllRequestsByRequestor, hasSize(2));
        assertThat(getAllRequestsByRequestor.get(0).getDescription(), equalTo(ir2.getDescription()));
        assertThat(getAllRequestsByRequestor.get(1).getDescription(), equalTo(ir1.getDescription()));

        // when
        List<ItemRequestDto> getAllRequestsExceptUser = (List<ItemRequestDto>) itemRequestService
                .getAllRequestsExceptUser(alice.getId());

        // then
        assertThat(getAllRequestsExceptUser, hasSize(1));
        assertThat(getAllRequestsExceptUser.getFirst().getDescription(), equalTo(ir3.getDescription()));
    }
}
