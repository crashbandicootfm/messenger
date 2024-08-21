package dev.crashbandicootfm.messenger.server.service.message;

import dev.crashbandicootfm.messenger.server.entity.Message;
import dev.crashbandicootfm.messenger.server.repository.MessageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MessageServiceImpl implements MessageService {

    @NotNull MessageRepository messageRepository;

    @Override
    public Message sendMessage(@NotNull Message message) {
        return messageRepository.save(message);
    }

    public List<Message> getMessages(@NotNull UUID uuid) {
        return messageRepository.findMessagesById(uuid);
    }
}
