package dev.crashbandicootfm.messenger.service.repository;

import dev.crashbandicootfm.messenger.service.model.ChatModel;
import org.springframework.data.repository.CrudRepository;

public interface ChatRepository extends CrudRepository<ChatModel, Long> {

}
