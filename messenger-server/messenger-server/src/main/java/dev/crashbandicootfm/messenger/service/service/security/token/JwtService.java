package dev.crashbandicootfm.messenger.service.service.security.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JwtService implements ClaimService, TokenFactory, TokenValidator {

    @NonFinal
    @Value("${jwt.secret}")
    String secret;

    @NonFinal
    @Value("${jwt.lifetime}")
    int lifetime;

    @NonFinal
    @Value("${jwt.refresh-lifetime}")
    int refreshLifetime;

    @Override
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    @Override
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    @Override
    public @NotNull String generateToken(@NotNull String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + lifetime))
                .signWith(getSignKey())
                .compact();
    }

    @Override
    public @NotNull String generateRefreshToken(@NotNull String username) {
        return Jwts.builder()
            .subject(username)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + refreshLifetime))
            .signWith(getSignKey())
            .compact();
    }

    @Override
    public boolean isValid(@NotNull String token) {
        try {
            Claims claims = extractClaims(token);

            boolean expired = claims.getExpiration().before(new Date(System.currentTimeMillis()));
            return !expired;
        } catch (JwtException e) {
            return false;
        }
    }

    @Override
    public boolean isRefreshTokenValid(@NotNull String token) {
        try {
            Claims claims = extractClaims(token);
            boolean expired = claims.getExpiration().before(new Date(System.currentTimeMillis()));
            return !expired;
        } catch (JwtException e) {
            return false;
        }
    }
}
