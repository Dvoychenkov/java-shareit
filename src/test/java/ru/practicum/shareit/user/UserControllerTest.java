package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @Test
    void createUser_returnsDto() throws Exception {
        // given
        NewUserDto newUserDto = new NewUserDto("john", "john@mail.com");
        UserDto userDto = new UserDto(1L, newUserDto.getName(), newUserDto.getEmail());

        when(userService.add(newUserDto))
                .thenReturn(userDto);

        // when/then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(newUserDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class));

        // verify
        verify(userService)
                .add(refEq(newUserDto));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void createUser_duplicateEmail_returns409() throws Exception {
        // given
        NewUserDto newUserDto = new NewUserDto("john", "john@mail.com");
        String errMsg = String.format("Пользователь с email %s уже существует", newUserDto.getEmail());

        when(userService.add(newUserDto))
                .thenThrow(new DuplicateException(errMsg));

        // when/then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(newUserDto)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is(errMsg)));

        // verify
        verify(userService)
                .add(refEq(newUserDto));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void createUser_emptyEmail_incorrectEmail_returns500() throws Exception {
        // given
        NewUserDto newUserDto = new NewUserDto("john", "     ");

        // when/then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(newUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        // given
        newUserDto = new NewUserDto("john", "john");

        // when/then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(newUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getUser_returnsDto() throws Exception {
        // given
        UserDto userDto = new UserDto(1L, "John", "john@mail.com");

        when(userService.find(userDto.getId()))
                .thenReturn(userDto);

        // when/then
        mockMvc.perform(get("/users/" + userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        // verify
        verify(userService)
                .find(eq(userDto.getId()));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getAllUsers_returnsListDtos() throws Exception {
        // given

        List<UserDto> users = List.of(
                new UserDto(1L, "Alice", "Alice@mail.com"),
                new UserDto(2L, "Bob", "Bob@mail.com")
        );

        when(userService.findAll())
                .thenReturn(users);

        // when/then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(users.size())));

        // verify
        verify(userService)
                .findAll();
        verifyNoMoreInteractions(userService);
    }

    @Test
    void updateUser_returnsDto() throws Exception {
        // given
        UserDto userDto = new UserDto(1L, "Joe", "joe@mail.com");
        UpdateUserDto updateUserDto = new UpdateUserDto("Johnny", "johnny@mail.com");
        UserDto updatedUserDto = new UserDto(userDto.getId(), updateUserDto.getName(), updateUserDto.getEmail());

        when(userService.save(userDto.getId(), updateUserDto))
                .thenReturn(updatedUserDto);

        // when/then
        mockMvc.perform(patch("/users/" + userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(updateUserDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updateUserDto.getName())))
                .andExpect(jsonPath("$.email", is(updateUserDto.getEmail())));

        // verify
        verify(userService)
                .save(eq(userDto.getId()), refEq(updateUserDto));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void removeUser_returnsOk() throws Exception {
        // given
        Long userId = 1L;
        doNothing().when(userService).remove(userId);

        // when/then
        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isOk());

        // verify
        verify(userService)
                .remove(eq(userId));
        verifyNoMoreInteractions(userService);
    }
}
