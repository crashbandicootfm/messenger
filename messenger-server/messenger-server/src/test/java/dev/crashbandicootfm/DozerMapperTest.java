package dev.crashbandicootfm;

import dev.crashbandicootfm.messenger.server.api.dto.request.UserRequest;
import dev.crashbandicootfm.messenger.server.entity.User;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DozerMapperTest {

    @NotNull Mapper mapper = DozerBeanMapperBuilder.buildDefault();

    @Test
    public void testMapping() {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("name");
        userRequest.setPassword("password");

        User user = mapper.map(userRequest, User.class);

        assertEquals("null", user.getUsername());
        assertEquals("password", user.getPassword());
    }
}
