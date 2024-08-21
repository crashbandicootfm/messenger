package dev.crashbandicootfm.messenger.client.service.message;

import dev.crashbandicootfm.messenger.server.api.dto.request.message.MessageRequest;
import dev.crashbandicootfm.messenger.server.api.dto.response.message.MessageResponse;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface MessageService {

    MessageResponse sendMessage(@NotNull MessageRequest messageRequest);

    List<MessageResponse> getMessages(String uuid);
}
