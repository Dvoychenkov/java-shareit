package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

import static ru.practicum.shareit.utils.ValidationUtils.requireFound;

@Service
@AllArgsConstructor
public class UserServiceBase implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto add(UserDto userDto) {
        User userToCreate = UserMapper.toUser(userDto);
        User createdUser = userRepository.add(userToCreate);
        return UserMapper.toUserDto(createdUser);
    }

    @Override
    public Collection<UserDto> findAll() {
        Collection<User> users = userRepository.findAll();
        return users.stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto find(Long id) {
        User user = getUserOrThrow(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto save(Long id, UserDto userDto) {
        getUserOrThrow(id);
        User userToSave = UserMapper.toUser(userDto);
        userToSave.setId(id);
        User savedUser = userRepository.save(userToSave);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public void remove(Long id) {
        getUserOrThrow(id);
        userRepository.remove(id);
    }

    private User getUserOrThrow(Long id) {
        return requireFound(userRepository.find(id), () -> "Пользователь с ID " + id + " не найден");
    }
}
