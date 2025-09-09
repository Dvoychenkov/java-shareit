package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.validation.IdValid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody NewUserDto newUserDto) {
        log.info("createUser. newUserDto: {}", newUserDto);
        return userClient.createUser(newUserDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@IdValid("userId") @PathVariable Long userId) {
        log.info("getUser. userId: {}", userId);
        return userClient.getUser(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
            @IdValid("userId") @PathVariable Long userId,
            @Valid @RequestBody UpdateUserDto updateUserDto
    ) {
        log.info("updateUser. userId: {}, updateUserDto: {}", userId, updateUserDto);
        return userClient.updateUser(userId, updateUserDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> removeUser(@IdValid("userId") @PathVariable Long userId) {
        log.info("removeUser. userId: {}", userId);
        return userClient.removeUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("getAllUsers.");
        return userClient.getAllUsers();
    }
}