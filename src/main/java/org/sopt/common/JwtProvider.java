package org.sopt.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {
    private final SecretKey secretKey;
    private final Long accessTokenExpiration;
    private final Long refreshTokenExpiration;

    public JwtProvider(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.access-token-expiration}") Long accessTokenExpiration,
                       @Value("${jwt.refresh-token-expiration}") Long refreshTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        if (accessTokenExpiration == null || accessTokenExpiration <= 0 ||
                refreshTokenExpiration == null || refreshTokenExpiration <= 0) {
            throw new IllegalArgumentException("토큰 유효 기간은 0보다 커야 합니다.");
        }
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String createAccessToken(Long memberId, Long sessionId) {
        return createToken(memberId, sessionId, "access", accessTokenExpiration);
    }

    public String createRefreshToken(Long memberId, Long sessionId) {
        return createToken(memberId, sessionId, "refresh", refreshTokenExpiration);
    }

    private String createToken(Long memberId, Long sessionId, String tokenUse, Long expiration) {
        if (memberId == null || memberId <= 0 || sessionId == null || sessionId <= 0) {
            throw new IllegalArgumentException("회원 ID와 세션 ID는 양수여야 합니다.");
        }
        Instant now = Instant.now();
        return Jwts.builder().subject(memberId.toString())
                .claim("sid", sessionId.toString()).claim("token_use", tokenUse)
                .id(UUID.randomUUID().toString()).issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expiration))).signWith(secretKey).compact();
    }

    public TokenInfo parseAccessToken(String token) { return parseToken(token, "access"); }
    public TokenInfo parseRefreshToken(String token) { return parseToken(token, "refresh"); }

    private TokenInfo parseToken(String token, String expectedUse) {
        Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        if (!expectedUse.equals(claims.get("token_use", String.class))) {
            throw new JwtException("토큰 용도가 올바르지 않습니다.");
        }
        String subject = claims.getSubject();
        String sessionId = claims.get("sid", String.class);
        String tokenId = claims.getId();
        Date expiresAt = claims.getExpiration();
        if (subject == null || sessionId == null || tokenId == null || tokenId.isBlank() || expiresAt == null) {
            throw new JwtException("필수 토큰 정보가 없습니다.");
        }
        try {
            Long memberId = Long.valueOf(subject);
            Long parsedSessionId = Long.valueOf(sessionId);
            if (memberId <= 0 || parsedSessionId <= 0) throw new JwtException("토큰의 ID가 올바르지 않습니다.");
            return new TokenInfo(memberId, parsedSessionId, tokenId, expiresAt.toInstant());
        } catch (NumberFormatException e) {
            throw new JwtException("토큰의 ID가 올바르지 않습니다.", e);
        }
    }

    public record TokenInfo(Long memberId, Long sessionId, String tokenId, Instant expiresAt) {}
}
