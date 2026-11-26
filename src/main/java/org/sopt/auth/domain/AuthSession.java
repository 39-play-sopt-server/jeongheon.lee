package org.sopt.auth.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class AuthSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionId;

    @Column(nullable = false, updatable = false)
    private Long memberId;

    @Column(nullable = false)
    private String refreshTokenHash;

    @Column(nullable = false)
    private Instant expiresAt;

    // 해당 리프레시토큰 폐기 시간, 존재하면 폐기된 세션
    // 만료됐거나 폐기된 세션은 갱신할 수 없다
    private Instant revokedAt;

    protected AuthSession() {}

    public AuthSession(Long memberId, String refreshTokenHash, Instant expiresAt, Instant now) {
        if (memberId == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }
        validateTokenInformation(refreshTokenHash, expiresAt, now);

        this.memberId = memberId;
        this.refreshTokenHash = refreshTokenHash;
        this.expiresAt = expiresAt;
    }

    public boolean isUsable(Instant now) {
        validateNow(now);
        return revokedAt == null && now.isBefore(expiresAt);
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public void rotateToken(String newTokenHash, Instant newExpiresAt, Instant now) {
        if (!isUsable(now)) {
            throw new IllegalStateException("만료되었거나 폐기된 세션은 갱신할 수 없습니다");
        }
        validateTokenInformation(newTokenHash, newExpiresAt, now);

        this.refreshTokenHash = newTokenHash;
        this.expiresAt = newExpiresAt;
    }

    public void revoke(Instant now) {
        validateNow(now);
        if (!isRevoked()) {
            this.revokedAt = now;
        }
    }

    // 토큰값이 존재하는지,
    private static void validateTokenInformation(String tokenHash, Instant expiresAt, Instant now) {
        validateNow(now);
        if (tokenHash == null || tokenHash.isBlank()) {
            throw new IllegalArgumentException("Refresh Token 해시는 필수입니다.");
        }
        if (expiresAt == null || !expiresAt.isAfter(now)) {
            throw new IllegalArgumentException("만료 시각은 현재보다 이후여야 합니다.");
        }
    }

    // 기준 시각이 존재하는지 검증
    private static void validateNow(Instant now) {
        if (now == null) {
            throw new IllegalArgumentException("기준 시각은 필수입니다.");
        }
    }

    public Long getSessionId() { return sessionId; }
    public Long getMemberId() { return memberId; }
    public String getRefreshTokenHash() { return refreshTokenHash; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getRevokedAt() { return revokedAt; }
}
