package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Value;

@Value
public class UpdateUserDto {
    String name;

    @Email(message = "Некорректный e-mail")
    String email;
}
