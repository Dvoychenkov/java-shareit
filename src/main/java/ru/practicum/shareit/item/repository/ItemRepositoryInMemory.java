package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository

public class ItemRepositoryInMemory implements ItemRepository {
    private long itemIdCnt = 0;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item add(Item item) {
        if (item == null) {
            return null;
        }
        item.setId(++itemIdCnt);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item save(Item item) {
        if (item == null) {
            return null;
        }
        if (!items.containsKey(item.getId())) {
            return null;
        }
        items.replace(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> find(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Collection<Item> findAllByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getOwner().equals(ownerId))
                .toList();
    }

    @Override
    public Collection<Item> searchItems(String nameSubstring, String descriptionSubstring, boolean isAvailable) {
        boolean doNameSearch = nameSubstring != null && !nameSubstring.isBlank();
        boolean doDescriptionSearch = descriptionSubstring != null && !descriptionSubstring.isBlank();

        return items.values().stream()
                .filter(Objects::nonNull)
                .filter(item -> {
                    if (item.isAvailable() != isAvailable) {
                        return false;
                    }
                    if (doNameSearch) {
                        String itemName = item.getName();
                        if (itemName != null && itemName.toLowerCase().contains(nameSubstring.toLowerCase())) {
                            return true;
                        }
                    }
                    if (doDescriptionSearch) {
                        String itemDesc = item.getDescription();
                        return itemDesc != null && itemDesc.toLowerCase().contains(descriptionSubstring.toLowerCase());
                    }
                    return false;
                })
                .toList();
    }
}
