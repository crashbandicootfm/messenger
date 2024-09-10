package dev.crashbandicootfm.messenger.service.service.security.filter;

import dev.crashbandicootfm.messenger.service.service.security.details.UserDetailsImpl;
import dev.crashbandicootfm.messenger.service.service.security.details.UsernameJwtAuthenticationToken;
import dev.crashbandicootfm.messenger.service.service.security.token.ClaimService;
import dev.crashbandicootfm.messenger.service.service.security.token.TokenValidator;
import dev.crashbandicootfm.messenger.service.service.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @NotNull TokenValidator tokenValidator;

    @NotNull ClaimService claimService;

    @NotNull UserService userService;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (!tokenValidator.isValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = claimService.extractUsername(token);

        UserDetails userDetails = userService.getUserOptional(username)
                .map(UserDetailsImpl::new)
                .orElse(null);

        if (userDetails == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = new UsernameJwtAuthenticationToken(userDetails);
        authentication.setAuthenticated(true);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
