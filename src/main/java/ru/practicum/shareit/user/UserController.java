package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.find(id);
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return userService.add(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@Valid @PathVariable Long id, @RequestBody UserDto userDto) {
        return userService.save(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable Long id) {
        userService.remove(id);
    }
}