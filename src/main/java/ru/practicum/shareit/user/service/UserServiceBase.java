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

import static ru.practicum.shareit.utils.ValidationUtils.requireFound;

@Service
@AllArgsConstructor
public class UserServiceBase implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto add(NewUserDto newUserDto) {
        validateEmailUniqueness(newUserDto.getEmail(), null);
        User userToCreate = UserMapper.toUser(newUserDto);

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
    public UserDto save(Long id, UpdateUserDto updateUserDto) {
        validateEmailUniqueness(updateUserDto.getEmail(), id);
        User userToSave = getUserOrThrow(id);

        UserMapper.updateUser(userToSave, updateUserDto);
        User savedUser = userRepository.save(userToSave);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public void remove(Long id) {
        getUserOrThrow(id);
        userRepository.remove(id);
    }

    @Override
    public User getUserOrThrow(Long id) {
        return requireFound(userRepository.find(id), () -> "Пользователь с ID " + id + " не найден");
    }

    private void validateEmailUniqueness(String currentUserEmail, Long currentUserId) {
        Optional<User> existingUser = userRepository.findByEmail(currentUserEmail);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(currentUserId)) {
            throw new DuplicateException("Пользователь с таким email уже существует");
        }
    }
}
