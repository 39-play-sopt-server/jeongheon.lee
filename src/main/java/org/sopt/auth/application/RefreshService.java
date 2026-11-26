package org.sopt.auth.application;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.sopt.auth.application.dto.LoginResult;
import org.sopt.auth.application.exception.InvalidRefreshTokenException;
import org.sopt.auth.domain.AuthSession;
import org.sopt.auth.domain.AuthSessionRepository;
import org.sopt.common.JwtProvider;
import org.sopt.member.domain.Member;
import org.sopt.member.domain.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshService {
    private final JwtProvider jwtProvider;
    private final AuthSessionRepository sessionRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public LoginResult refresh(String refreshToken) {
        JwtProvider.TokenInfo info = validateToken(refreshToken);
        AuthSession session = sessionRepository.findByIdForUpdate(info.sessionId())
                .orElseThrow(InvalidRefreshTokenException::new);

        // 잠금을 기다리는 동안 토큰이 만료됐을 수도 있으므로 다시 확인한다.
        info = validateToken(refreshToken);
        Instant now = Instant.now();
        if (!session.getMemberId().equals(info.memberId()) || !session.isUsable(now)) {
            throw new InvalidRefreshTokenException();
        }
        Member member = memberRepository.findById(info.memberId())
                .orElseThrow(InvalidRefreshTokenException::new);
        if (member.isWithdrawn() || !MessageDigest.isEqual(
                session.getRefreshTokenHash().getBytes(StandardCharsets.UTF_8),
                RefreshTokenHash.hash(refreshToken).getBytes(StandardCharsets.UTF_8))) {
            throw new InvalidRefreshTokenException();
        }

        String newRefresh = jwtProvider.createRefreshToken(info.memberId(), info.sessionId());
        Instant refreshExpiresAt = jwtProvider.parseRefreshToken(newRefresh).expiresAt();
        String access = jwtProvider.createAccessToken(info.memberId(), info.sessionId());
        Instant accessExpiresAt = jwtProvider.parseAccessToken(access).expiresAt();
        session.rotateToken(RefreshTokenHash.hash(newRefresh), refreshExpiresAt, Instant.now());
        return new LoginResult(access, accessExpiresAt, newRefresh, refreshExpiresAt);
    }

    private JwtProvider.TokenInfo validateToken(String token) {
        try {
            if (token == null || token.isBlank()) throw new InvalidRefreshTokenException();
            return jwtProvider.parseRefreshToken(token);
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidRefreshTokenException();
        }
    }
}
