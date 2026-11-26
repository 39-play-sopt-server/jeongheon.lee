package org.sopt.auth.application;

import lombok.RequiredArgsConstructor;
import org.sopt.common.JwtProvider;
import org.sopt.auth.domain.AuthSession;
import org.sopt.auth.domain.AuthSessionRepository;
import org.sopt.auth.application.dto.LoginResult;
import org.sopt.member.domain.Member;
import org.sopt.member.domain.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthSessionRepository sessionRepository;

    @Transactional
    public LoginResult login(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        if (member.isWithdrawn()) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // IDENTITY 방식에서는 저장한 후 세션 ID가 생성됨
        Instant now = Instant.now();
        AuthSession session = sessionRepository.save(new AuthSession(
                member.getMemberId(), "pending", now.plusSeconds(60), now));
        String refreshToken = jwtProvider.createRefreshToken(member.getMemberId(), session.getSessionId());
        JwtProvider.TokenInfo refreshInfo = jwtProvider.parseRefreshToken(refreshToken);
        session.rotateToken(hashToken(refreshToken), refreshInfo.expiresAt(), now);
        String accessToken = jwtProvider.createAccessToken(member.getMemberId(), session.getSessionId());
        Instant accessExpiresAt = jwtProvider.parseAccessToken(accessToken).expiresAt();
        return new LoginResult(accessToken, accessExpiresAt, refreshToken, refreshInfo.expiresAt());
    }

    private String hashToken(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256을 사용할 수 없습니다.", e);
        }
    }
}
