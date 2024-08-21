package dev.crashbandicootfm.messenger.service.controller.v1;

import dev.crashbandicootfm.messenger.service.api.request.CreateChatRequest;
import dev.crashbandicootfm.messenger.service.api.response.ChatResponse;
import dev.crashbandicootfm.messenger.service.cqrs.command.CreateChatCommand;
import dev.crashbandicootfm.messenger.service.model.ChatModel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pw.qubique.mediatr.Mediatr;

@RequiredArgsConstructor
@RestController("/api/v1/chats")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ChatController {

  @NotNull Mediatr mediatr;

  @PostMapping("/")
  public ChatResponse create(@NotNull @RequestBody CreateChatRequest request) {
    CreateChatCommand command = new CreateChatCommand(1L, request.getName());
    ChatModel model = mediatr.dispatch(command, ChatModel.class);
    //TODO map with dozer
    return new ChatResponse();
  }

}
