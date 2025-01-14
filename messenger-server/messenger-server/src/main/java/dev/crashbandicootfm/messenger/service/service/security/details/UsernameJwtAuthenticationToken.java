package dev.crashbandicootfm.messenger.service.service.security.details;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UsernameJwtAuthenticationToken extends AbstractAuthenticationToken {

    @NotNull UserDetails userDetails;

    public UsernameJwtAuthenticationToken(@NotNull UserDetails userDetails) {
        super(userDetails.getAuthorities());
        this.userDetails = userDetails;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userDetails;
    }
}
