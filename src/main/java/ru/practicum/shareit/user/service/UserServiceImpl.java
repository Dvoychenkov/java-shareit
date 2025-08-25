package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

import static ru.practicum.shareit.utils.ValidationUtils.requireExists;
import static ru.practicum.shareit.utils.ValidationUtils.requireFound;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private static final String MSG_USER_BY_ID_NOT_EXISTS = "Пользователь с ID %d не найден";
    private static final String MSG_USER_EMAIL_DUPLICATE = "Пользователь с email %s уже существует";
    private static final String MSG_USER_EMAIL_IS_BLANK = "Email не должен быть пустым";

    @Override
    @Transactional
    public UserDto add(NewUserDto newUserDto) {
        if (newUserDto.getEmail() == null || newUserDto.getEmail().isBlank()) {
            throw new IllegalArgumentException(MSG_USER_EMAIL_IS_BLANK);
        }
        validateEmailUniqueness(newUserDto.getEmail());
        User userToCreate = userMapper.toUser(newUserDto);

        User createdUser = userRepository.save(userToCreate);
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
    @Transactional
    public UserDto save(Long id, UpdateUserDto updateUserDto) {
        validateEmailUniqueness(updateUserDto.getEmail());
        User userToSave = getUserOrThrow(id);

        userMapper.updateUser(updateUserDto, userToSave);
        User savedUser = userRepository.save(userToSave);
        return userMapper.toUserDto(savedUser);
    }

    @Override
    @Transactional
    public void remove(Long id) {
        existsByIdOrThrow(id);
        userRepository.deleteById(id);
    }

    @Override
    public User getUserOrThrow(Long id) {
        return requireFound(userRepository.findById(id), () -> String.format(MSG_USER_BY_ID_NOT_EXISTS, id));
    }

    @Override
    public void existsByIdOrThrow(Long id) {
        requireExists(userRepository.existsById(id), () -> String.format(MSG_USER_BY_ID_NOT_EXISTS, id));
    }

    private void validateEmailUniqueness(String currentUserEmail) {
        if (currentUserEmail == null || currentUserEmail.isBlank()) {
            return;
        }

        if (userRepository.existsByEmail(currentUserEmail)) {
            throw new DuplicateException(String.format(MSG_USER_EMAIL_DUPLICATE, currentUserEmail));
        }
    }
}
