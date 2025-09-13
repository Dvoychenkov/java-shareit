package ru.practicum.shareit.booking.dto;

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
class NewBookingDtoJsonTest {

    private final JacksonTester<NewBookingDto> jacksonTester;
    private static final DateTimeFormatter dtFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    void serialize() throws Exception {
        // given
        LocalDateTime start = LocalDateTime.of(2030, 1, 2, 10, 0, 0);
        LocalDateTime end = start.plusDays(1);
        Long itemId = 5L;
        NewBookingDto newBookingDto = new NewBookingDto(start, end, itemId);

        // when
        JsonContent<NewBookingDto> result = jacksonTester.write(newBookingDto);

        // then
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(dtFormatter.format(start));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(dtFormatter.format(end));
        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(itemId.intValue());
    }

    @Test
    void deserialize() throws Exception {
        // given
        LocalDateTime start = LocalDateTime.of(2030, 1, 2, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2030, 1, 3, 10, 0, 0);
        Long itemId = 5L;

        String raw = new JSONObject()
                .put("start", dtFormatter.format(start))
                .put("end", dtFormatter.format(end))
                .put("itemId", itemId)
                .toString();

        // when
        NewBookingDto parsed = jacksonTester.parseObject(raw);

        // then
        assertThat(parsed.getStart()).isEqualTo(start);
        assertThat(parsed.getEnd()).isEqualTo(end);
        assertThat(parsed.getItemId()).isEqualTo(itemId);
    }
}
