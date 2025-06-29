package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;

import static ru.practicum.shareit.utils.ValidationUtils.requireExists;
import static ru.practicum.shareit.utils.ValidationUtils.requireFound;

@Service
@AllArgsConstructor
public class UserServiceBase implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private static final String USER_BY_ID_NOT_EXISTS = "Пользователь с ID %d не найден";

    @Override
    public UserDto add(NewUserDto newUserDto) {
        validateEmailUniqueness(newUserDto.getEmail(), null);
        User userToCreate = userMapper.toUser(newUserDto);

        User createdUser = userRepository.add(userToCreate);
        return userMapper.toUserDto(createdUser);
    }

    @Override
    public Collection<UserDto> findAll() {
        Collection<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto find(Long id) {
        User user = getUserOrThrow(id);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto save(Long id, UpdateUserDto updateUserDto) {
        validateEmailUniqueness(updateUserDto.getEmail(), id);
        User userToSave = getUserOrThrow(id);

        userMapper.updateUser(updateUserDto, userToSave);
        User savedUser = userRepository.save(userToSave);
        return userMapper.toUserDto(savedUser);
    }

    @Override
    public void remove(Long id) {
        existsByIdOrThrow(id);
        userRepository.remove(id);
    }

    @Override
    public User getUserOrThrow(Long id) {
        return requireFound(userRepository.find(id), () -> String.format(USER_BY_ID_NOT_EXISTS, id));
    }

    @Override
    public void existsByIdOrThrow(Long id) {
        requireExists(userRepository.existsById(id), () -> String.format(USER_BY_ID_NOT_EXISTS, id));
    }

    private void validateEmailUniqueness(String currentUserEmail, Long currentUserId) {
        Optional<User> existingUser = userRepository.findByEmail(currentUserEmail);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(currentUserId)) {
            throw new DuplicateException("Пользователь с таким email уже существует");
        }
    }
}
