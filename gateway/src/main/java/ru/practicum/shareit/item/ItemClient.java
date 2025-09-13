package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> createItem(Long userId, NewItemDto newItemDto) {
        return post("", userId, newItemDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, UpdateItemDto updateItemDto) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );

        return patch("/{itemId}", userId, parameters, updateItemDto);
    }

    public ResponseEntity<Object> getItem(Long userId, Long itemId) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );

        return get("/{itemId}", userId, parameters);
    }

    public ResponseEntity<Object> getAllItemsOfOwner(Long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> searchItems(Long userId, String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );

        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, NewCommentDto newCommentDto) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );

        return post("/{itemId}/comment", userId, parameters, newCommentDto);
    }
}
