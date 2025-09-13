package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingTimeDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    ItemService itemService;

    @Test
    void create_withRequestId_returnsDto() throws Exception {
        // given
        Long xSharerUserId = 1L;
        Long requestId = 42L;

        NewItemDto newItemDto = new NewItemDto("Шуруповёрт", "Мощный", true, requestId);

        ItemDto itemDto = new ItemDto(
                2L, newItemDto.getName(), newItemDto.getDescription(), newItemDto.getAvailable(), List.of());

        when(itemService.add(newItemDto, xSharerUserId))
                .thenReturn(itemDto);

        // when/then
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", xSharerUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(newItemDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.requestId").doesNotExist());

        // verify
        verify(itemService)
                .add(refEq(newItemDto), eq(xSharerUserId));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void create_withRequestId_notFound_returns404() throws Exception {
        // given
        Long xSharerUserId = 1L;
        Long requestId = 42L;

        NewItemDto newItemDto = new NewItemDto("Шуруповёрт", "Мощный", true, requestId);

        when(itemService.add(newItemDto, xSharerUserId))
                .thenThrow(new NotFoundException("Запрос вещи не найден"));

        // when/then
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", xSharerUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(newItemDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        // verify
        verify(itemService)
                .add(refEq(newItemDto), eq(xSharerUserId));
        verifyNoMoreInteractions(itemService);
    }


    @Test
    void getItem_returnsItemWithBookingsDto_noBookings() throws Exception {
        // given
        Long xSharerUserId = 1L;
        Long itemId = 10L;
        LocalDateTime now = LocalDateTime.now();

        List<CommentDto> comments = List.of(
                new CommentDto(1L, "Офигенная вещь", "Alice", now)
        );
        ItemWithBookingsDto itemWithBookingsDto = new ItemWithBookingsDto(
                itemId, "Перфоратор", "Мощнейший", true,
                null, null, comments
        );

        when(itemService.find(itemId))
                .thenReturn(itemWithBookingsDto);

        // when/then
        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", xSharerUserId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemId.intValue())))
                .andExpect(jsonPath("$.name", is(itemWithBookingsDto.getName())))
                .andExpect(jsonPath("$.lastBooking", is(nullValue())))
                .andExpect(jsonPath("$.nextBooking", is(nullValue())))
                .andExpect(jsonPath("$.comments", hasSize(comments.size())))
                .andExpect(jsonPath("$.comments[0].text", is(comments.getFirst().getText())));

        // verify
        verify(itemService)
                .find(eq(itemId));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getItemsByOwner_returnsListDtos_withBookings() throws Exception {
        // given
        Long ownerId = 1L;
        LocalDateTime now = LocalDateTime.now();

        BookingTimeDto last = new BookingTimeDto(now.minusDays(2), now.minusDays(1));
        BookingTimeDto next = new BookingTimeDto(now.plusDays(1), now.plusDays(2));
        List<ItemWithBookingsDto> itemWithBookingsDtos = List.of(
                new ItemWithBookingsDto(10L, "Перфоратор", "Мощнейший",
                        true, last, next, List.of())
        );

        when(itemService.findAllWithBookingsByOwnerId(ownerId))
                .thenReturn(itemWithBookingsDtos);

        // when/then
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(itemWithBookingsDtos.size())))
                .andExpect(jsonPath("$[0].lastBooking.start", notNullValue()))
                .andExpect(jsonPath("$[0].nextBooking.start", notNullValue()));

        // verify
        verify(itemService)
                .findAllWithBookingsByOwnerId(eq(ownerId));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void addComment_returnsDto() throws Exception {
        // given
        Long userId = 2L;
        Long itemId = 10L;
        LocalDateTime now = LocalDateTime.now();

        NewCommentDto newCommentDto = new NewCommentDto("Отличная штука");
        CommentDto commentDto = new CommentDto(1L, newCommentDto.getText(), "Bob", now);

        when(itemService.addComment(itemId, userId, newCommentDto))
                .thenReturn(commentDto);

        // when/then
        mockMvc.perform(post("/items/{id}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(newCommentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId().intValue())))
                .andExpect(jsonPath("$.text", is(newCommentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));

        // verify
        verify(itemService)
                .addComment(eq(itemId), eq(userId), refEq(newCommentDto));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void search_withText_returnsListDtos() throws Exception {
        // given
        Long xSharerUserId = 1L;
        String text = "дрель";
        List<ItemDto> items = List.of(
                new ItemDto(1L, "Дрель-9000", "мощная", true, List.of()),
                new ItemDto(2L, "Мини-дрель", "аккумуляторная", true, List.of())
        );

        when(itemService.searchAvailableItems(text))
                .thenReturn(items);

        // when/then
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", xSharerUserId)
                        .param("text", text)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(items.size())))
                .andExpect(jsonPath("$[0].name", is(items.getFirst().getName())));

        // verify
        verify(itemService)
                .searchAvailableItems(eq(text));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void search_blankText_returnsEmpty_andNoServiceCall() throws Exception {
        // given
        Long xSharerUserId = 1L;
        String text = "   ";

        when(itemService.searchAvailableItems(text))
                .thenReturn(List.of());

        // when/then
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", xSharerUserId)
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("[]"));

        // verify
        verify(itemService)
                .searchAvailableItems(eq(text));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void updateItem_ok_returnsUpdatedDto() throws Exception {
        // given
        Long itemId = 10L;
        Long ownerId = 1L;

        UpdateItemDto updateItemDto = new UpdateItemDto("Новое имя", "Новая жизнь", false);
        ItemDto itemDto = new ItemDto(
                itemId, updateItemDto.getName(), updateItemDto.getDescription(), updateItemDto.getAvailable(), List.of());

        when(itemService.save(itemId, updateItemDto, ownerId))
                .thenReturn(itemDto);

        // when/then
        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(updateItemDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemId), Long.class))
                .andExpect(jsonPath("$.name", is(updateItemDto.getName())))
                .andExpect(jsonPath("$.description", is(updateItemDto.getDescription())));

        // verify
        verify(itemService)
                .save(eq(itemId), refEq(updateItemDto), eq(ownerId));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void updateItem_notOwner_returns403() throws Exception {
        // given
        Long itemId = 10L;
        Long notOwnerId = 2L;

        UpdateItemDto updateItemDto = new UpdateItemDto("Новое имя", "Новая жизнь", true);

        String errMsg = String.format("Пользователь с ID %d не является владельцем вещи", notOwnerId);
        when(itemService.save(itemId, updateItemDto, notOwnerId))
                .thenThrow(new ForbiddenException(errMsg));

        // when/then
        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", notOwnerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(updateItemDto)))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is(errMsg)));
        ;

        // verify
        verify(itemService)
                .save(eq(itemId), refEq(updateItemDto), eq(notOwnerId));
        verifyNoMoreInteractions(itemService);
    }
}
