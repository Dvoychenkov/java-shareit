package ru.practicum.shareit.item.repository;

import org.apache.commons.lang3.StringUtils;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.function.Predicate;

public class ItemRepositoryInMemory {
    private long itemIdCnt = 0;
    private final Map<Long, Item> items = new HashMap<>();

    public Item add(Item item) {
        if (item == null) {
            return null;
        }
        item.setId(++itemIdCnt);
        items.put(item.getId(), item);
        return item;
    }

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

    public Optional<Item> find(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    public Collection<Item> findAllByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getOwner().equals(ownerId))
                .toList();
    }

    public Collection<Item> searchItems(String nameSubstring, String descriptionSubstring, boolean isAvailable) {
        Predicate<Item> searchItemFilter = buildSearchItemFilter(nameSubstring, descriptionSubstring, isAvailable);

        return items.values().stream()
                .filter(Objects::nonNull)
                .filter(searchItemFilter)
                .toList();
    }

    private Predicate<Item> buildSearchItemFilter(String nameSubstring, String descriptionSubstring, boolean isAvailable) {
        boolean doNameSearch = StringUtils.isNotBlank(nameSubstring);
        boolean doDescriptionSearch = StringUtils.isNotBlank(descriptionSubstring);

        return item -> {
            if (item.isAvailable() != isAvailable) {
                return false;
            }
            if (doNameSearch && StringUtils.containsIgnoreCase(item.getName(), nameSubstring)) {
                return true;
            }
            return doDescriptionSearch && StringUtils.containsIgnoreCase(item.getDescription(), descriptionSubstring);
        };
    }
}
