package ru.practicum.shareit.user.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @org.springframework.beans.factory.annotation.Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceIT {

    private final EntityManager em;
    private final UserService userService;

    @Test
    void add_persistsUser_andUniqueEmail() {
        // given
        NewUserDto newUserDto = new NewUserDto("john", "john@mail.com");

        // when
        UserDto saved = userService.add(newUserDto);

        // then
        TypedQuery<User> query = em.createQuery("""
                select u
                from User u
                where u.id=:id
                """, User.class);
        User user = query.setParameter("id", saved.getId())
                .getSingleResult();

        assertThat(user.getEmail(), is(newUserDto.getEmail()));

        // when/then
        NewUserDto newSameUserDto = new NewUserDto("john", "john@mail.com");
        assertThrows(DuplicateException.class,
                () -> userService.add(newSameUserDto));
    }

    @Test
    void save_updatesFields() {
        // given
        NewUserDto newUserDto = new NewUserDto("John", "John@mail.com");
        UserDto saved = userService.add(newUserDto);

        // when
        UpdateUserDto updateUserDto = new UpdateUserDto("Johnny", "Johnny@mail.com");
        UserDto updated = userService.save(saved.getId(), updateUserDto);

        // then
        TypedQuery<User> query = em.createQuery("""
                select u
                from User u
                where u.id=:id
                """, User.class);
        User uodatedUser = query.setParameter("id", updated.getId())
                .getSingleResult();

        assertThat(uodatedUser.getName(), is(updateUserDto.getName()));
        assertThat(uodatedUser.getEmail(), is(updateUserDto.getEmail()));
    }

    @Test
    void findAll_returnsDtoList() {
        // given
        List<UserDto> usersDtos = List.of(
                userService.add(new NewUserDto("alice", "alice@mail.com")),
                userService.add(new NewUserDto("bob", "bob@mail.com"))
        );

        // when
        Collection<UserDto> foundUsersDtos = userService.findAll();

        // then
        assertThat(foundUsersDtos, hasSize(usersDtos.size()));
        assertThat(
                foundUsersDtos.stream()
                        .map(UserDto::getEmail)
                        .toList(),
                containsInAnyOrder(usersDtos.stream()
                        .map(UserDto::getEmail)
                        .toArray())
        );

        // when
        TypedQuery<User> query = em.createQuery("""
                select u
                from User u
                """, User.class);
        List<User> users = query.getResultList();

        // then
        assertThat(users, hasSize(usersDtos.size()));
        assertThat(
                users.stream()
                        .map(User::getEmail)
                        .toList(),
                containsInAnyOrder(usersDtos.stream()
                        .map(UserDto::getEmail)
                        .toArray())
        );
    }

    @Test
    void find_returnsDto() {
        // given
        UserDto saved = userService.add(new NewUserDto("john", "john@mail.com"));

        // when
        UserDto found = userService.find(saved.getId());

        // then
        assertThat(found.getId(), equalTo(saved.getId()));
        assertThat(found.getEmail(), equalTo(saved.getEmail()));
    }

    @Test
    void remove_andThrowsNotFoundException() {
        // given
        UserDto saved = userService.add(new NewUserDto("kate", "kate@mail.com"));

        // when
        userService.remove(saved.getId());

        // then
        assertThrows(NotFoundException.class, () -> userService.find(saved.getId()));

        // when
        TypedQuery<Long> query = em.createQuery("""
                select count(u)
                from User u
                where u.id=:id
                """, Long.class);
        Long cnt = query.setParameter("id", saved.getId())
                .getSingleResult();

        // then
        assertThat(cnt, is(0L));
    }
}
