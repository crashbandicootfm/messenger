package dev.crashbandicootfm.messenger.client.service.chat;

import dev.crashbandicootfm.messenger.service.api.request.CreateChatRequest;
import dev.crashbandicootfm.messenger.service.api.response.ChatResponse;
import org.jetbrains.annotations.NotNull;

public interface ChatService {

    ChatResponse create(@NotNull CreateChatRequest request);
}
