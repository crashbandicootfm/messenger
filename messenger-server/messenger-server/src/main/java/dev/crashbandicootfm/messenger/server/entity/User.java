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
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class User {

    @NonNull
    @Column(nullable = false, length = 20)
    @NotNull String name;

    @NonNull
    @Column(nullable = false, length = 30)
    @NotNull String password;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    @NonFinal
    int ident;
}
