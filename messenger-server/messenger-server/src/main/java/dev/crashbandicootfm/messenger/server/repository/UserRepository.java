package dev.crashbandicootfm.messenger.server.repository;

import dev.crashbandicootfm.messenger.server.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
