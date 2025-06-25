package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto add(UserDto userDto);

    Collection<UserDto> findAll();

    UserDto find(Long id);

    UserDto save(Long id, UserDto userDto);

    void remove(Long id);
}
