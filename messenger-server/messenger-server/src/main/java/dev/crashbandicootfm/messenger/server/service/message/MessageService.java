package dev.crashbandicootfm.messenger.server.service.message;

import dev.crashbandicootfm.messenger.server.entity.Message;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message sendMessage(@NotNull Message message);

    List<Message> getMessages(@NotNull UUID uuid);
}
