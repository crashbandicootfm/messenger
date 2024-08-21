package dev.crashbandicootfm.messenger.server.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@Table(name = "messages")
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Message {

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    UUID id;

    @NonNull
    @Column(nullable = false)
    @NotNull String message;
}
