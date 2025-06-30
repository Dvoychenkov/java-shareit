package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Item add(Item item);

    Item save(Item item);

    Optional<Item> find(Long id);

    Collection<Item> findAllByOwnerId(Long ownerId);

    Collection<Item> searchItems(String nameLike, String descriptionLike, boolean isAvailable);
}
