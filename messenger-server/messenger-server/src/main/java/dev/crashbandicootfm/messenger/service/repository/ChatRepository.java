package dev.crashbandicootfm.messenger.service.repository;

import dev.crashbandicootfm.messenger.service.model.ChatModel;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends CrudRepository<ChatModel, Long> {

  @NotNull Optional<ChatModel> findByName(@NotNull String name);

  @NotNull List<ChatModel> findAllByIdIn(@NotNull List<Long> ids);

  void deleteById(@NotNull Long id);

  @Query("SELECT c FROM ChatModel c WHERE c.id IN (" +
      "SELECT cu.chatId FROM ChatUserStatusModel cu WHERE cu.userId = :userId)")
  List<ChatModel> findUserChats(@Param("userId") Long userId);

  List<ChatModel> findAllByName(String name);
}
