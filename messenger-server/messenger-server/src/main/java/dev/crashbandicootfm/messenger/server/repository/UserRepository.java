package dev.crashbandicootfm.messenger.server.repository;

import dev.crashbandicootfm.messenger.server.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @NotNull Optional<User> findUserByUsername(@NotNull String username);
}
