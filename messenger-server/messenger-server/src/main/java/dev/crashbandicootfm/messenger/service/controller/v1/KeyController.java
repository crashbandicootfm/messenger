package dev.crashbandicootfm.messenger.service.controller.v1;

import dev.crashbandicootfm.messenger.service.service.key.KeyService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/keys")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class KeyController {

    @NotNull KeyService keyService;

    @GetMapping("/client/{clientId}")
    public ResponseEntity<String> getClientPublicKey(@PathVariable Long clientId) {
        String publicKey = keyService.getClientPublicKey(clientId);
        if (publicKey == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(publicKey);
    }

    @PostMapping("/store-client-key")
    public ResponseEntity<String> storeClientPublicKey(@RequestBody Map<Long, String> request) {
        Long clientId = Long.valueOf(request.get("clientId"));
        String publicKey = request.get("publicKey");

        if (clientId == null || publicKey == null) {
            return ResponseEntity.badRequest().body("Invalid request");
        }

        keyService.storeClientPublicKey(clientId, publicKey);
        return ResponseEntity.ok("Client public key stored successfully");
    }
}