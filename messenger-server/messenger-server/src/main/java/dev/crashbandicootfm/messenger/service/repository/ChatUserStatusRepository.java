package dev.crashbandicootfm.messenger.service.repository;

import dev.crashbandicootfm.messenger.service.model.ChatUserStatusModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatUserStatusRepository extends JpaRepository<ChatUserStatusModel, Long> {
    Optional<ChatUserStatusModel> findByChatIdAndUserId(Long chatId, Long userId);

    Optional<ChatUserStatusModel> findByChatId(Long chatId);
}