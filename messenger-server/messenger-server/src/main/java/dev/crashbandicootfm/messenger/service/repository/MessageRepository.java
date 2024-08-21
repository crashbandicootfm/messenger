package dev.crashbandicootfm.messenger.service.repository;


import dev.crashbandicootfm.messenger.service.model.MessageModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepository extends CrudRepository<MessageModel, Long> {

  @NotNull @Unmodifiable List<MessageModel> findAllByChatId(@NotNull Long chatId);

  @NotNull @Unmodifiable List<MessageModel> findAllByChatIdAndUserId(@NotNull Long chatId, @NotNull Long userId);

}
