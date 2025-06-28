package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

import static ru.practicum.shareit.utils.ValidationUtils.requireFound;

@Service
@AllArgsConstructor
public class ItemServiceBase implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto add(NewItemDto newItemDto, Long ownerId) {
        userService.getUserOrThrow(ownerId);
        Item itemToCreate = ItemMapper.toItem(newItemDto);
        itemToCreate.setOwner(ownerId);

        Item createdItem = itemRepository.add(itemToCreate);
        return ItemMapper.toItemDto(createdItem);
    }

    @Override
    public ItemDto save(Long id, UpdateItemDto updateItemDto, Long ownerId) {
        userService.getUserOrThrow(ownerId);
        Item itemToSave = getItemOrThrow(id);
        if (!itemToSave.getOwner().equals(ownerId)) {
            throw new ForbiddenException("Редактировать вещь может только её владелец");
        }

        ItemMapper.updateItem(itemToSave, updateItemDto);
        Item savedItem = itemRepository.save(itemToSave);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto find(Long id) {
        Item item = getItemOrThrow(id);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> findAllByOwnerId(Long ownerId) {
        userService.getUserOrThrow(ownerId);

        Collection<Item> ownerItems = itemRepository.findAllByOwnerId(ownerId);
        return ownerItems.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> searchAvailableItems(String searchText) {
        Collection<Item> itemsSearchResult = itemRepository.searchItems(searchText, searchText, true);
        return itemsSearchResult.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public Item getItemOrThrow(Long id) {
        return requireFound(itemRepository.find(id), () -> "Вещь с ID " + id + " не найдена");
    }
}
