package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class NewUserDtoJsonTest {

    private final JacksonTester<NewUserDto> jacksonTester;

    @Test
    void serialize() throws Exception {
        // given
        NewUserDto newUserDto = new NewUserDto("john", "john@mail.com");

        // when
        JsonContent<NewUserDto> result = jacksonTester.write(newUserDto);

        // then
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(newUserDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.email")
                .isEqualTo(newUserDto.getEmail());
    }

    @Test
    void deserialize() throws Exception {
        // given
        String name = "Jane";
        String email = "jane@mail.com";

        JSONObject userJson = new JSONObject()
                .put("name", name)
                .put("email", email);

        String raw = userJson.toString();

        // when
        NewUserDto newUserDto = jacksonTester.parseObject(raw);

        // then
        assertThat(newUserDto.getName())
                .isEqualTo(name);
        assertThat(newUserDto.getEmail())
                .isEqualTo(email);
    }
}
