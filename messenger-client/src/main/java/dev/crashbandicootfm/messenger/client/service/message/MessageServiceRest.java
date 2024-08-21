package dev.crashbandicootfm.messenger.client.service.message;

import dev.crashbandicootfm.messenger.server.api.dto.request.message.MessageRequest;
import dev.crashbandicootfm.messenger.server.api.dto.response.message.MessageResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MessageServiceRest implements MessageService {

    @NotNull RestTemplate restTemplate;

    private static final String URL = "http://localhost:8080/api/message/";

    @Override
    public MessageResponse sendMessage(@NotNull MessageRequest messageRequest) {
        return restTemplate.postForEntity(URL, messageRequest, MessageResponse.class).getBody();
    }

    public List<MessageResponse> getMessages(String uuid) {
        String url = URL + "chat/" + uuid;
        return Arrays.asList(Objects.requireNonNull(restTemplate.getForObject(url, MessageResponse[].class)));
    }
}
