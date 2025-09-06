package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.shareit.utils.ValidationUtils.requireExists;
import static ru.practicum.shareit.utils.ValidationUtils.requireFound;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    private final UserService userService;

    private final ItemRequestMapper itemRequestMapper;

    private static final String MSG_REQUEST_BY_ID_NOT_EXISTS = "Запрос вещи с ID %d не найден";

    @Transactional
    @Override
    public ItemRequestDto add(Long requestorId, NewItemRequestDto newItemRequestDto) {
        User requestor = userService.getUserOrThrow(requestorId);

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(newItemRequestDto, requestor);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest saved = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestDto(saved);
    }

    @Override
    public Collection<ItemRequestDto> getAllRequestsByRequestor(Long requestorId) {
        userService.existsByIdOrThrow(requestorId);

        Collection<ItemRequest> requests = itemRequestRepository.findByRequestor_IdOrderByCreatedDesc(requestorId);
        Map<ItemRequest, Collection<Item>> answersForRequests = collectAnswersForRequests(requests);
        return itemRequestMapper.mapToItemRequestDtos(answersForRequests);
    }

    @Override
    public Collection<ItemRequestDto> getAllRequestsExceptUser(Long userId) {
        userService.existsByIdOrThrow(userId);

        Collection<ItemRequest> requests = itemRequestRepository.findByRequestor_IdNotOrderByCreatedDesc(userId);
        Map<ItemRequest, Collection<Item>> answersForRequests = collectAnswersForRequests(requests);
        return itemRequestMapper.mapToItemRequestDtos(answersForRequests);
    }

    @Override
    public ItemRequestDto get(Long userId, Long requestId) {
        userService.existsByIdOrThrow(userId);

        ItemRequest request = getItemRequestOrThrow(requestId);
        Collection<Item> items = itemRepository.findAllByRequest(requestId);
        return itemRequestMapper.toItemRequestDto(request, items);
    }

    @Override
    public ItemRequest getItemRequestOrThrow(Long id) {
        return requireFound(itemRequestRepository.findById(id), () -> String.format(MSG_REQUEST_BY_ID_NOT_EXISTS, id));
    }

    @Override
    public void existsByIdOrThrow(Long id) {
        requireExists(itemRequestRepository.existsById(id), () -> String.format(MSG_REQUEST_BY_ID_NOT_EXISTS, id));
    }

    private Map<ItemRequest, Collection<Item>> collectAnswersForRequests(Collection<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return Map.of();
        }

        List<Long> requestsIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        Collection<Item> answersItems = itemRepository.findAllByRequestIn(requestsIds);

        Map<Long, List<Item>> requestsIdsToItems = answersItems.stream()
                .collect(Collectors.groupingBy(Item::getRequest));

        return requests.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        request -> requestsIdsToItems.getOrDefault(request.getId(), List.of())
                ));
    }
}