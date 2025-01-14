package dev.crashbandicootfm.messenger.client.service.chat;

import dev.crashbandicootfm.messenger.service.api.request.CreateChatRequest;
import dev.crashbandicootfm.messenger.service.api.response.ChatResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ChatServiceRest implements ChatService {

    private static final String URL = "http://localhost:8080/api/v1/chats/";

    @NotNull RestTemplate restTemplate;

    @Override
    public ChatResponse create(@NotNull CreateChatRequest request, @NotNull String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<CreateChatRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ChatResponse> response = restTemplate.postForEntity(URL, entity, ChatResponse.class);

        return response.getBody();
    }

    @Override
    public ChatResponse findByName(@NotNull String name, @NotNull String jwtToken) {
        String url = URL + "/find?name=" + name;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<ChatResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, ChatResponse.class);

        return response.getBody();
    }
}
