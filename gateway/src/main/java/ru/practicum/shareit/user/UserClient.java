package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import java.util.Map;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> createUser(NewUserDto newUserDto) {
        return post("", newUserDto);
    }

    public ResponseEntity<Object> getUser(Long userId) {
        Map<String, Object> parameters = Map.of(
                "userId", userId
        );

        return get("/{userId}", null, parameters);
    }

    public ResponseEntity<Object> updateUser(Long userId, UpdateUserDto updateUserDto) {
        Map<String, Object> parameters = Map.of(
                "userId", userId
        );

        return patch("/{userId}", null, parameters, updateUserDto);
    }

    public ResponseEntity<Object> removeUser(Long userId) {
        Map<String, Object> parameters = Map.of(
                "userId", userId
        );

        return delete("/{userId}", null, parameters);
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("");
    }
}
