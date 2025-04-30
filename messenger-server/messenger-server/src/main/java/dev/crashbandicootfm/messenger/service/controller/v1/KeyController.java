package dev.crashbandicootfm.messenger.service.controller.v1;

import dev.crashbandicootfm.messenger.service.api.request.KeyRequest;
import dev.crashbandicootfm.messenger.service.exception.user.UserException;
import dev.crashbandicootfm.messenger.service.model.UserModel;
import dev.crashbandicootfm.messenger.service.repository.UserRepository;
import dev.crashbandicootfm.messenger.service.service.key.KeyService;
import dev.crashbandicootfm.messenger.service.service.user.UserService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/keys")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class KeyController {

    @NotNull KeyService keyService;

    @NotNull UserService userService;

    @NotNull UserRepository userRepository;

    @GetMapping("/get-client-key/{clientId}")
    public ResponseEntity<String> getClientKey(@PathVariable Long clientId) {
        String publicKey = keyService.getClientPublicKey(clientId);
        if (publicKey == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(publicKey);
    }

    @PostMapping("/store-client-key")
    public ResponseEntity<Void> storeClientKey(@RequestBody KeyRequest request) {
        if (request.getClientId() == null || request.getPublicKey() == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            String username = getUsernameByClientId(request.getClientId());

            userService.savePublicKey(username, request.getPublicKey());

            return ResponseEntity.ok().build();
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private String getUsernameByClientId(Long clientId) throws UserException {
        return userRepository.findById(clientId)
            .map(UserModel::getUsername)
            .orElseThrow(() -> new UserException("User not found!"));
    }
}