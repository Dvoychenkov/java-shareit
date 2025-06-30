package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class NewUserDto {
    @NotBlank(message = "Пустое имя пользователя")
    String name;

    @NotBlank(message = "Пустой e-mail пользователя")
    @Email(message = "Некорректный e-mail")
    String email;
}
