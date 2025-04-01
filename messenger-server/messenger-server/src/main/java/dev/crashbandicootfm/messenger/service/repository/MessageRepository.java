package dev.crashbandicootfm.messenger.service.repository;

import dev.crashbandicootfm.messenger.service.model.MessageModel;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends CrudRepository<MessageModel, Long> {

  @NotNull @Unmodifiable Page<MessageModel> findAllByChatId(@NotNull Long chatId, @NotNull Pageable pageable);

  @NotNull @Unmodifiable List<MessageModel> findAllByChatId(@NotNull Long chatId);

  Optional<MessageModel> findTopByChatIdOrderBySentAtDesc(Long chatId);

  @Modifying
  @Query("UPDATE MessageModel m SET m.readByUsers = :readByUsers WHERE m.id = :messageId")
  void markMessageAsRead(@Param("messageId") Long messageId, @Param("readByUsers") List<Long> readByUsers);

  @Query("SELECT COUNT(m) FROM MessageModel m WHERE m.chatId = :chatId AND " +
      "m.id > (SELECT COALESCE(MAX(cus.lastReadMessageId), 0) FROM ChatUserStatusModel cus WHERE cus.chatId = :chatId AND cus.userId = :userId)")
  Long countUnreadMessages(@Param("chatId") Long chatId, @Param("userId") Long userId);

  @Query("SELECT MAX(m.id) FROM MessageModel m WHERE m.chatId = :chatId")
  Long getLastMessageId(@Param("chatId") Long chatId);
}
