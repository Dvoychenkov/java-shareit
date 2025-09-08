package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.IdValid;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody NewUserDto newUserDto) {
        return userService.add(newUserDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@IdValid("userId") @PathVariable Long userId) {
        return userService.find(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(
            @IdValid("userId") @PathVariable Long userId,
            @Valid @RequestBody UpdateUserDto updateUserDto
    ) {
        return userService.save(userId, updateUserDto);
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@IdValid("userId") @PathVariable Long userId) {
        userService.remove(userId);
    }

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        return userService.findAll();
    }
}