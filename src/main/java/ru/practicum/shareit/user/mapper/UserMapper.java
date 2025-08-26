package ru.practicum.shareit.user.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User toUser(NewUserDto newUserDto);

    UserDto toUserDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(UpdateUserDto updateUserDto, @MappingTarget User user);
}
