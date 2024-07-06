package dev.crashbandicootfm.messenger.server.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.jetbrains.annotations.NotNull;

@Entity
@Getter
@Setter
@ToString
@Table(name = "app_user")
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @NonNull
    @Column(updatable = false, nullable = false, unique = true)
    @NotNull String username;

    @NonNull
    @Column(nullable = false)
    @NotNull String password;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    @NonFinal
    long id;
}
