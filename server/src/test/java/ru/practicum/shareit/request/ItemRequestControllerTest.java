package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Test
    void createItemRequest_returnsDto() throws Exception {
        // given
        Long xSharerUserId = 1L;
        Long anotherUserId = 2L;
        LocalDateTime now = LocalDateTime.now();

        NewItemRequestDto newItemRequestDto = new NewItemRequestDto("Нужен перфоратор");
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                xSharerUserId, newItemRequestDto.getDescription(), now, List.of(
                new ItemAnswerDto(1L, "Перфоратор", anotherUserId)
        ));

        when(itemRequestService.add(xSharerUserId, newItemRequestDto))
                .thenReturn(itemRequestDto);

        // when/then
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", xSharerUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(newItemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));

        // verify
        verify(itemRequestService)
                .add(
                        eq(xSharerUserId),
                        refEq(newItemRequestDto)
                );
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getAllItemsRequestsByRequestor_returnsDtoList() throws Exception {
        // given
        Long xSharerUserId = 1L;
        Long anotherUserId = 2L;
        LocalDateTime now = LocalDateTime.now();

        List<ItemRequestDto> itemRequestDtos = List.of(
                new ItemRequestDto(2L, "Нужен перфоратор", now, List.of(
                        new ItemAnswerDto(1L, "Перфоратор", anotherUserId)
                )),
                new ItemRequestDto(3L, "Нужен шуруповёрт", now.plusMinutes(1L), List.of(
                        new ItemAnswerDto(1L, "Шуруповёрт", anotherUserId)
                ))
        );

        when(itemRequestService.getAllRequestsByRequestor(xSharerUserId))
                .thenReturn(itemRequestDtos);

        // when/then
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", xSharerUserId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(itemRequestDtos.size())))
                .andExpect(jsonPath("$[*].id",
                                containsInRelativeOrder(
                                        itemRequestDtos.stream()
                                                .map(dto -> dto.getId().intValue())
                                                .toArray()
                                )
                        )
                )
                .andExpect(jsonPath("$[*].items[*].ownerId", everyItem(not(xSharerUserId.intValue()))))
        ;

        // verify
        verify(itemRequestService)
                .getAllRequestsByRequestor(
                        eq(xSharerUserId)
                );
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getAllItemsRequests_returnsDtoList() throws Exception {
        // given
        Long xSharerUserId = 1L;
        Long anotherUserId = 2L;
        LocalDateTime now = LocalDateTime.now();

        List<ItemRequestDto> itemRequestDtos = List.of(
                new ItemRequestDto(2L, "Нужен перфоратор", now, List.of(
                        new ItemAnswerDto(1L, "Перфоратор", anotherUserId)
                )),
                new ItemRequestDto(3L, "Нужен шуруповёрт", now.plusMinutes(1L), List.of(
                        new ItemAnswerDto(1L, "Шуруповёрт", anotherUserId)
                ))
        );

        when(itemRequestService.getAllRequestsExceptUser(xSharerUserId))
                .thenReturn(itemRequestDtos);

        // when/then
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", xSharerUserId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(itemRequestDtos.size())))
                .andExpect(jsonPath("$[*].id",
                                containsInRelativeOrder(
                                        itemRequestDtos.stream()
                                                .map(dto -> dto.getId().intValue())
                                                .toArray()
                                )
                        )
                );

        // verify
        verify(itemRequestService)
                .getAllRequestsExceptUser(
                        eq(xSharerUserId)
                );
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getItemRequest_returnsDto() throws Exception {
        // given
        Long xSharerUserId = 1L;
        Long anotherUserId = 2L;
        Long requestId = 999L;
        LocalDateTime now = LocalDateTime.now();

        NewItemRequestDto newItemRequestDto = new NewItemRequestDto("Нужен перфоратор");
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                xSharerUserId, newItemRequestDto.getDescription(), now, List.of(
                new ItemAnswerDto(1L, "Перфоратор", anotherUserId)
        ));

        when(itemRequestService.get(xSharerUserId, requestId))
                .thenReturn(itemRequestDto);

        // when/then
        mockMvc.perform(get("/requests/" + requestId.intValue())
                        .header("X-Sharer-User-Id", xSharerUserId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class));

        // verify
        verify(itemRequestService)
                .get(
                        eq(xSharerUserId),
                        eq(requestId)
                );
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getItemRequest_notFound_returns404() throws Exception {
        // given
        Long xSharerUserId = 1L;
        Long requestId = 999L;

        when(itemRequestService.get(xSharerUserId, requestId))
                .thenThrow(new NotFoundException("Запрос не найден"));

        // when/then
        mockMvc.perform(get("/requests/" + requestId.intValue())
                        .header("X-Sharer-User-Id", xSharerUserId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        // verify
        verify(itemRequestService).get(xSharerUserId, requestId);
        verifyNoMoreInteractions(itemRequestService);
    }

}
