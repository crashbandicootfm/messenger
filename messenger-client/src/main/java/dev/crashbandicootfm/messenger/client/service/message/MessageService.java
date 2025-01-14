package dev.crashbandicootfm.messenger.client.service.message;

import dev.crashbandicootfm.messenger.service.api.request.MessageRequest;
import dev.crashbandicootfm.messenger.service.api.response.MessageResponse;
import java.util.List;

public interface MessageService {

  void sendMessage(MessageRequest messageRequest, String token);

  List<MessageResponse> getMessagesByChatId(Long chatId, String token);
}
