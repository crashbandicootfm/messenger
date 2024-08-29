package dev.crashbandicootfm.messenger.service.controller.v1;

import com.github.dozermapper.core.Mapper;
import dev.crashbandicootfm.messenger.service.api.request.CreateChatRequest;
import dev.crashbandicootfm.messenger.service.api.response.ChatResponse;
import dev.crashbandicootfm.messenger.service.cqrs.command.CreateChatCommand;
import dev.crashbandicootfm.messenger.service.model.ChatModel;
import dev.crashbandicootfm.messenger.service.service.security.details.UserDetailsImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pw.qubique.mediatr.Mediatr;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ChatController {

  @NotNull Mediatr mediatr;

  @NotNull Mapper mapper;

  @PostMapping("/")
  public ChatResponse create(
          @NotNull @RequestBody CreateChatRequest request,
          @AuthenticationPrincipal UserDetailsImpl principal
  ) {
    CreateChatCommand command = new CreateChatCommand(principal.getId(), request.getName());
    ChatModel model = mediatr.dispatch(command, ChatModel.class);
    return mapper.map(model, ChatResponse.class);
  }
}
