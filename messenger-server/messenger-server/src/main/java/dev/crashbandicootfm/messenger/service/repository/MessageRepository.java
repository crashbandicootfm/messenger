package dev.crashbandicootfm.messenger.service.repository;

import dev.crashbandicootfm.messenger.service.model.MessageModel;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<MessageModel, Long> {

  @NotNull @Unmodifiable Page<MessageModel> findAllByChatId(@NotNull Long chatId, @NotNull Pageable pageable);

  @NotNull @Unmodifiable List<MessageModel> findAllByChatId(@NotNull Long chatId);
}
