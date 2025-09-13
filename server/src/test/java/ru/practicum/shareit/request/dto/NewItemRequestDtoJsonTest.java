package ru.practicum.shareit.request.dto;

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
class NewItemRequestDtoJsonTest {

    private final JacksonTester<NewItemRequestDto> jacksonTester;

    @Test
    void serialize() throws Exception {
        // given
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto("Нужен пылесос");

        // when
        JsonContent<NewItemRequestDto> result = jacksonTester.write(newItemRequestDto);

        // then
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(newItemRequestDto.getDescription());
    }

    @Test
    void deserialize() throws Exception {
        // given
        String description = "Нужен пылесос";

        JSONObject itemJson = new JSONObject()
                .put("description", description);

        String raw = itemJson.toString();

        // when
        NewItemRequestDto newItemRequestDto = jacksonTester.parseObject(raw);

        // then
        assertThat(newItemRequestDto.getDescription()).isEqualTo(description);
    }
}
