package dev.crashbandicootfm.messenger.client.service.message;

import dev.crashbandicootfm.messenger.service.api.request.MessageRequest;
import dev.crashbandicootfm.messenger.service.api.response.MessageResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MessageServiceRest implements MessageService {

  private static final String URL = "http://localhost:8080/api/v1/messages/";

  @NotNull RestTemplate restTemplate;

  @Override
  public void sendMessage(MessageRequest messageRequest, String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);

    HttpEntity<MessageRequest> entity = new HttpEntity<>(messageRequest, headers);

    restTemplate.postForEntity(URL, entity, Void.class);
  }

  @Override
  public List<MessageResponse> getMessagesByChatId(Long chatId, String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<List<MessageResponse>> response = restTemplate.exchange(
        URL + "chat/" + chatId,
        HttpMethod.GET,
        entity,
        new ParameterizedTypeReference<List<MessageResponse>>() {}
    );

    return response.getBody();
  }
}
