package com.fleetmanagementservice.kernel.utils;

import com.fleetmanagementservice.core.constant.enums.ErrorCode;
import com.fleetmanagementservice.core.constant.exception.AppException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
@Getter
public class JwtUtil {

    private final Key key;
    private final long expirationMs;
    private static final long CLOCK_SKEW_SECONDS = 60;

    public JwtUtil(
            @Value("${client.secret.key}") String secret,
            @Value("${jwt.expiration-ms:3600000}") long expirationMs
    ) {
        Assert.hasText(secret, "client.secret.key must not be blank");
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public Jws<Claims> validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .setAllowedClockSkewSeconds(CLOCK_SKEW_SECONDS)
                    .build()
                    .parseClaimsJws(token);
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new AppException(ErrorCode.AUTH_TOKEN_INVALID, "Bad signature or malformed token");
        } catch (ExpiredJwtException e) {
            throw new AppException(ErrorCode.AUTH_TOKEN_INVALID, "Token expired");
        } catch (UnsupportedJwtException e) {
            throw new AppException(ErrorCode.AUTH_TOKEN_INVALID, "Unsupported token");
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.AUTH_TOKEN_INVALID, "Empty token");
        }
    }
}
