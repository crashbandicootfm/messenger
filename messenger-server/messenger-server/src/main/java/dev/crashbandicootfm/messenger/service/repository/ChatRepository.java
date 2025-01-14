package dev.crashbandicootfm.messenger.service.repository;

import dev.crashbandicootfm.messenger.service.model.ChatModel;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

public interface ChatRepository extends CrudRepository<ChatModel, Long> {

  @NotNull Optional<ChatModel> findByName(@NotNull String name);
}
