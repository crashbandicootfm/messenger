package dev.crashbandicootfm.messenger.client.service.chat;

import dev.crashbandicootfm.messenger.service.api.request.CreateChatRequest;
import dev.crashbandicootfm.messenger.service.api.response.ChatResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ChatServiceRest implements ChatService {

    private static final String URL = "http://localhost:8080/api/v1/chats/";

    @NotNull RestTemplate restTemplate;

    @Override
    public ChatResponse create(@NotNull CreateChatRequest request) {
        return restTemplate.postForEntity(URL, request, ChatResponse.class).getBody();
    }
}
