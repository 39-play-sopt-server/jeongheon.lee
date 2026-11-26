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
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다."); // 비밀번호 틀린건 좀 알려주는게 좋을듯
        }
        if (member.isWithdrawn()) {
            throw new IllegalArgumentException("이미 탈퇴된 회원입니다.");
        }

        // IDENTITY 방식에서는 저장한 후 세션 ID가 생성됨 -> 저장 후 조회
        Instant now = Instant.now();
        AuthSession session = sessionRepository.save(
                new AuthSession(
                member.getMemberId(),
                "pending",
                now.plusSeconds(60),
                now)
        );
        // createRefreshToken
        String refreshToken = jwtProvider.createRefreshToken(
                member.getMemberId(),
                session.getSessionId()
        );
        // JWT 문자열을 읽고 검증한 뒤, 토큰 안의 정보를 TokenInfo로 반환
        JwtProvider.TokenInfo refreshInfo = jwtProvider.parseRefreshToken(refreshToken);
        // 세션에 저장된 토큰 정보를 새 정보로 교체
        session.rotateToken(
                RefreshTokenHash.hash(refreshToken),
                refreshInfo.expiresAt(),
                now
        );
        String accessToken = jwtProvider.createAccessToken(
                member.getMemberId(),
                session.getSessionId()
        );
        Instant accessExpiresAt = jwtProvider.parseAccessToken(accessToken).expiresAt();
        return new LoginResult(accessToken, accessExpiresAt, refreshToken, refreshInfo.expiresAt());
    }

}
