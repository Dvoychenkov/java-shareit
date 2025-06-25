package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getEmail(),
                user.getName()
        );
    }

    public static User toUser(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        return user;
    }

    public static void updateUser(User user, UserDto userDto) {
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            user.setEmail(userDto.getEmail());
        }
    }
}
