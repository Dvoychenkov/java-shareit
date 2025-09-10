package ru.practicum.shareit.item.dto;

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
class NewCommentDtoJsonTest {

    private final JacksonTester<NewCommentDto> jacksonTester;

    @Test
    void serialize() throws Exception {
        // given
        NewCommentDto newCommentDto = new NewCommentDto("Отличная вещь!");

        // when
        JsonContent<NewCommentDto> result = jacksonTester.write(newCommentDto);

        // then
        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo(newCommentDto.getText());
    }

    @Test
    void deserialize() throws Exception {
        // given
        String text = "Отличная вещь!";

        JSONObject commentJson = new JSONObject()
                .put("text", text);

        String raw = commentJson.toString();

        // when
        NewCommentDto newCommentDto = jacksonTester.parseObject(raw);

        // then
        assertThat(newCommentDto.getText())
                .isEqualTo(text);
    }
}
