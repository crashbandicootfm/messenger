package dev.crashbandicootfm.messenger.server.repository;

import dev.crashbandicootfm.messenger.server.entity.Message;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends CrudRepository<Message, UUID> {

    @NotNull List<Message> findMessagesById(@NotNull UUID uuid);

}
