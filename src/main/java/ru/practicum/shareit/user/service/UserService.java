package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    UserDto add(NewUserDto newUserDto);

    Collection<UserDto> findAll();

    UserDto find(Long id);

    UserDto save(Long id, UpdateUserDto updateUserDto);

    void remove(Long id);

    User getUserOrThrow(Long id);

    void existsByIdOrThrow(Long id);
}
