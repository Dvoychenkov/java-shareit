package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.List;

import static ru.practicum.shareit.utils.ValidationUtils.requireFound;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceBase implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;

    private static final String MSG_ITEM_BY_ID_NOT_EXISTS = "Вещь с ID %d не найдена";
    private static final String MSG_INCORRECT_ITEM_OWNER = "Пользователь с ID %d не является владельцем вещи";

    @Override
    @Transactional
    public ItemDto add(NewItemDto newItemDto, Long ownerId) {
        userService.existsByIdOrThrow(ownerId);
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
    public ItemDto find(Long id) {
        Item item = getItemOrThrow(id);
        return itemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> findAllByOwnerId(Long ownerId) {
        userService.existsByIdOrThrow(ownerId);

        Collection<Item> ownerItems = itemRepository.findAllByOwner(ownerId);
        return ownerItems.stream()
                .map(itemMapper::toItemDto)
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
