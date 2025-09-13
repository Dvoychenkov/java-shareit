package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentDtoJsonTest {

    private final JacksonTester<CommentDto> jacksonTester;
    private static final DateTimeFormatter dtFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    void serialize() throws Exception {
        // given
        CommentDto commentDto = new CommentDto(1L, "Ok!", "Bob",
                LocalDateTime.of(2030, 1, 1, 12, 0));

        // when
        JsonContent<CommentDto> out = jacksonTester.write(commentDto);

        // then
        assertThat(out).extractingJsonPathNumberValue("$.id")
                .isEqualTo(commentDto.getId().intValue());
        assertThat(out).extractingJsonPathStringValue("$.text")
                .isEqualTo(commentDto.getText());
        assertThat(out).extractingJsonPathStringValue("$.authorName")
                .isEqualTo(commentDto.getAuthorName());
        assertThat(out).extractingJsonPathStringValue("$.created")
                .isEqualTo(dtFormatter.format(commentDto.getCreated()));
    }

    @Test
    void deserialize() throws Exception {
        // given
        Long commentId = 1L;
        String commentText = "ok";
        String authorName = "Bob";
        LocalDateTime created = LocalDateTime.of(2030, 1, 1, 12, 0);
        String createdStr = dtFormatter.format(created);

        String raw = new JSONObject()
                .put("id", commentId.intValue())
                .put("text", commentText)
                .put("authorName", authorName)
                .put("created", createdStr)
                .toString();

        // when
        CommentDto parsed = jacksonTester.parseObject(raw);

        // then
        assertThat(parsed.getId()).isEqualTo(commentId);
        assertThat(parsed.getText()).isEqualTo(commentText);
        assertThat(parsed.getAuthorName()).isEqualTo(authorName);
        assertThat(parsed.getCreated()).isEqualTo(created);
    }
}
