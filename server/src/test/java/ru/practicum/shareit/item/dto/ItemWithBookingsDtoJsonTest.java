package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemWithBookingsDtoJsonTest {

    private final JacksonTester<ItemWithBookingsDto> jacksonTester;
    private static final DateTimeFormatter dtFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    void serialize() throws Exception {
        // given
        ItemWithBookingsDto itemWithBookingsDto = new ItemWithBookingsDto(
                10L, "Перфоратор", "Мощнейший", true,
                null, null,
                List.of(
                        new CommentDto(1L, "Топчик", "Alice",
                                LocalDateTime.of(2030, 1, 1, 10, 0))
                )
        );

        // when
        JsonContent<ItemWithBookingsDto> result = jacksonTester.write(itemWithBookingsDto);

        // then
        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemWithBookingsDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemWithBookingsDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemWithBookingsDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemWithBookingsDto.isAvailable());
        assertThat(result).extractingJsonPathValue("$.lastBooking")
                .isNull();
        assertThat(result).extractingJsonPathValue("$.nextBooking")
                .isNull();

        assertThat(result).extractingJsonPathArrayValue("$.comments")
                .hasSize(itemWithBookingsDto.getComments().size());
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id")
                .isEqualTo(itemWithBookingsDto.getComments().getFirst().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text")
                .isEqualTo(itemWithBookingsDto.getComments().getFirst().getText());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName")
                .isEqualTo(itemWithBookingsDto.getComments().getFirst().getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created")
                .isNotNull();
    }

    @Test
    void deserialize() throws Exception {
        // given
        long itemWithBookingsDtoId = 10L;
        String itemWithBookingsDtoName = "Перфоратор";
        String itemWithBookingsDtoDescription = "Мощнейший";
        boolean itemWithBookingsDtoAvailable = true;
        String itemWithBookingsDtoBookingsDates = null;

        long commentDtoId = 1L;
        String commentDtoText = "Топчик";
        String commentDtoAuthorName = "Alice";
        LocalDateTime commentDtoCreated = LocalDateTime.of(2030, 1, 1, 10, 0);
        String commentDtoCreatedStr = dtFormatter.format(commentDtoCreated);

        String raw = String.format("""
                {
                    "id": %d,
                    "name": "%s",
                    "description": "%s",
                    "available": %b,
                    "lastBooking": %s,
                    "nextBooking": %s,
                    "comments": [
                        {
                            "id": %d,
                            "text": "%s",
                            "authorName": "%s",
                            "created": "%s"
                        }
                    ]
                }
                """, (int) itemWithBookingsDtoId, itemWithBookingsDtoName, itemWithBookingsDtoDescription,
                itemWithBookingsDtoAvailable, itemWithBookingsDtoBookingsDates, itemWithBookingsDtoBookingsDates,
                (int) commentDtoId, commentDtoText, commentDtoAuthorName, commentDtoCreatedStr);

        // when
        ItemWithBookingsDto parsed = jacksonTester.parseObject(raw);

        // then
        assertThat(parsed.getId()).isEqualTo(itemWithBookingsDtoId);
        assertThat(parsed.getName()).isEqualTo(itemWithBookingsDtoName);
        assertThat(parsed.getDescription()).isEqualTo(itemWithBookingsDtoDescription);
        assertThat(parsed.isAvailable()).isTrue();
        assertThat(parsed.getLastBooking()).isNull();
        assertThat(parsed.getNextBooking()).isNull();

        assertThat(parsed.getComments()).hasSize(1);
        assertThat(parsed.getComments().getFirst().getId()).isEqualTo(commentDtoId);
        assertThat(parsed.getComments().getFirst().getText()).isEqualTo(commentDtoText);
        assertThat(parsed.getComments().getFirst().getAuthorName()).isEqualTo(commentDtoAuthorName);
        assertThat(parsed.getComments().getFirst().getCreated()).isEqualTo(commentDtoCreated);
    }
}
