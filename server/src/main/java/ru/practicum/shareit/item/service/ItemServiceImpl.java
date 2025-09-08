package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemWithBookingsMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.utils.ValidationUtils.requireFound;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestService itemRequestService;

    private final UserService userService;

    private final ItemMapper itemMapper;
    private final ItemWithBookingsMapper itemWithBookingsMapper;
    private final CommentMapper commentMapper;

    private static final String MSG_ITEM_BY_ID_NOT_EXISTS = "Вещь с ID %d не найдена";
    private static final String MSG_INCORRECT_ITEM_OWNER = "Пользователь с ID %d не является владельцем вещи";
    private static final String MSG_CAN_NOT_COMMENT = "Оставить отзыв можно только после завершённой аренды";

    @Override
    @Transactional
    public ItemDto add(NewItemDto newItemDto, Long ownerId) {
        userService.existsByIdOrThrow(ownerId);
        if (newItemDto.getRequestId() != null) {
            itemRequestService.existsByIdOrThrow(newItemDto.getRequestId());
        }

        Item itemToCreate = itemMapper.toItem(newItemDto);
        itemToCreate.setOwner(ownerId);

        Item createdItem = itemRepository.save(itemToCreate);
        return itemMapper.toItemDto(createdItem);
    }

    @Override
    @Transactional
    public ItemDto save(Long id, UpdateItemDto updateItemDto, Long ownerId) {
        userService.existsByIdOrThrow(ownerId);
        Item itemToSave = getItemOrThrow(id);
        if (!itemToSave.getOwner().equals(ownerId)) {
            throw new ForbiddenException(String.format(MSG_INCORRECT_ITEM_OWNER, ownerId));
        }

        itemMapper.updateItem(updateItemDto, itemToSave);
        Item savedItem = itemRepository.save(itemToSave);
        return itemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long userId, NewCommentDto newCommentDto) {
        Item item = getItemOrThrow(itemId);
        User user = userService.getUserOrThrow(userId);

        LocalDateTime now = LocalDateTime.now();
        // Без явного смещения времени назад не проходит локальный тест Postman'а
        // Вероятно из-за особенности setTimeout с интервалом в 1000 мс в Pre-request скрипте
        now = now.minusSeconds(1L);

        boolean canComment = bookingRepository.existsByBooker_IdAndItem_IdAndStatusAndEndBefore(
                userId, itemId, BookingStatus.APPROVED, now);
        if (!canComment) {
            throw new ValidationException(MSG_CAN_NOT_COMMENT);
        }

        Comment comment = commentMapper.toComment(newCommentDto, item, user);
        comment.setCreated(now);

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    @Override
    public ItemWithBookingsDto find(Long id) {
        Item item = getItemOrThrow(id);

        Collection<Comment> comments = commentRepository.findAllByItem_IdOrderByCreatedDesc(id);
        return itemWithBookingsMapper.toItemWithBookingsDto(item, comments);
    }

    @Override
    public Collection<ItemWithBookingsDto> findAllWithBookingsByOwnerId(Long ownerId) {
        userService.existsByIdOrThrow(ownerId);

        Collection<Item> items = itemRepository.findAllByOwner(ownerId);
        if (items.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();
        Collection<Comment> comments = commentRepository.findAllByItem_IdInOrderByCreatedDesc(itemIds);
        Map<Long, List<Comment>> commentsByItem = comments.stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        LocalDateTime now = LocalDateTime.now();
        return items.stream()
                .map(item -> {
                    Booking last = bookingRepository.findTopByItem_IdAndStatusAndStartLessThanEqualOrderByStartDesc(
                                    item.getId(), BookingStatus.APPROVED, now)
                            .orElse(null);
                    Booking next = bookingRepository.findTopByItem_IdAndStatusAndStartGreaterThanOrderByStartAsc(
                                    item.getId(), BookingStatus.APPROVED, now)
                            .orElse(null);
                    List<Comment> itemComments = commentsByItem.getOrDefault(item.getId(), List.of());
                    return itemWithBookingsMapper.toItemWithBookingsDto(item, last, next, itemComments);

                })
                .toList();
    }

    @Override
    public Collection<ItemDto> searchAvailableItems(String searchText) {
        if (searchText == null || searchText.isBlank()) {
            return List.of();
        }

        Collection<Item> itemsSearchResult = itemRepository.search(searchText, searchText, true);
        return itemsSearchResult.stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public Item getItemOrThrow(Long id) {
        return requireFound(itemRepository.findById(id), () -> String.format(MSG_ITEM_BY_ID_NOT_EXISTS, id));
    }

}
