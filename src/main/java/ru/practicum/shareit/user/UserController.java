package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.IdValid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getUser(@IdValid() @PathVariable Long id) {
        return userService.find(id);
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody NewUserDto newUserDto) {
        return userService.add(newUserDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@IdValid() @PathVariable Long id, @Valid @RequestBody UpdateUserDto updateUserDto) {
        return userService.save(id, updateUserDto);
    }

    @DeleteMapping("/{id}")
    public void removeUser(@IdValid() @PathVariable Long id) {
        userService.remove(id);
    }
}