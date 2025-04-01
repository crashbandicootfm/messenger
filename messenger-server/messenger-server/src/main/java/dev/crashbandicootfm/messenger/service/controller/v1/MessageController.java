package dev.crashbandicootfm.messenger.service.controller.v1;

import com.github.dozermapper.core.Mapper;
import dev.crashbandicootfm.mediator.Mediatr;
import dev.crashbandicootfm.messenger.service.api.request.MessageRequest;
import dev.crashbandicootfm.messenger.service.api.response.MessageResponse;
import dev.crashbandicootfm.messenger.service.cqrs.command.CreateFileMessageCommand;
import dev.crashbandicootfm.messenger.service.cqrs.command.CreateMessageCommand;
import dev.crashbandicootfm.messenger.service.model.MessageModel;
import dev.crashbandicootfm.messenger.service.model.UserModel;
import dev.crashbandicootfm.messenger.service.service.file.FileStorageService;
import dev.crashbandicootfm.messenger.service.service.message.MessageService;
import dev.crashbandicootfm.messenger.service.service.security.details.UserDetailsImpl;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MessageController {

    @NotNull Mediatr mediatr;

    @NotNull Mapper mapper;

    @NotNull MessageService messageService;

    @PostMapping("/")
    public MessageResponse create(
            @NotNull @RequestBody MessageRequest request,
            @AuthenticationPrincipal UserDetailsImpl principal
    ) {
        CreateMessageCommand command = new CreateMessageCommand(
                request.getMessage(),
                request.getChatId(),
                principal.getId()
        );
        MessageModel model = mediatr.dispatch(command, MessageModel.class);
        return mapper.map(model, MessageResponse.class);
    }

    @GetMapping("/chat/{chatId}")
    public List<MessageResponse> getChatMessages(@PathVariable Long chatId) {
        List<MessageModel> messages = messageService.getMessagesByChatId(chatId);

        return messages.stream()
            .map(message -> {
                UserModel user = messageService.getUserById(message.getCreatedBy());
                return new MessageResponse(
                    message.getId(),
                    message.getMessage(),
                    message.getSentAt(),
                    message.getCreatedBy(),
                    message.getChatId(),
                    user.getUsername()
                );
            }).collect(Collectors.toList());
    }

    @DeleteMapping("/mess/{messageId}")
    public void deleteChatMessages(@PathVariable Long messageId) {
        System.out.println("AAAAAAAAAAAA");
        messageService.deleteMessageById(messageId);
        System.out.println("Deleted message: " + messageId);
    }

    @PostMapping("/mess/{messageId}/read")
    public void markMessageAsRead(@PathVariable Long messageId, @AuthenticationPrincipal UserDetailsImpl principal) {
        messageService.markMessageAsRead(messageId, principal.getId());
    }

    @PostMapping("/upload-file")
    public String uploadFile(
        @RequestParam("file") MultipartFile file,
        @AuthenticationPrincipal UserDetailsImpl principal
    ) throws IOException {
        if (file.isEmpty()) {
            return "No file selected!";
        }

        // Задайте путь для сохранения файла
        String uploadDir = "uploads/";
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir + fileName);

        // Создание директории, если не существует
        Files.createDirectories(path.getParent());

        // Сохраняем файл на сервере
        Files.copy(file.getInputStream(), path);

        // Возвращаем URL к файлу
        String fileUrl = "/uploads/" + fileName; // Ссылка на файл

        return fileUrl;
    }
}
