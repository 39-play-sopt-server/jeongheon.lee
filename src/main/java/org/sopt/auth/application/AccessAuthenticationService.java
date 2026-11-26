package org.sopt.auth.application;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.sopt.auth.domain.AuthSessionRepository;
import org.sopt.common.JwtProvider;
import org.sopt.member.domain.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AccessAuthenticationService {
    private final JwtProvider jwtProvider;
    private final AuthSessionRepository sessions;
    private final MemberRepository members;

    @Transactional(readOnly = true)
    // Access Token의 서명, 만료 시각, 토큰 용도와 필요한 정보를 검증
    public JwtProvider.TokenInfo authenticate(String token) {
        JwtProvider.TokenInfo info = jwtProvider.parseAccessToken(token);
        
        boolean activeSession = sessions.findById(info.sessionId())
                .filter(session -> info.memberId().equals(session.getMemberId())) // 토큰의 회원 ID와 세션의 회원 ID가 동일한가
                .filter(session -> session.isUsable(Instant.now())) // 폐기되거나 만료된 세션이 아닌가
                .isPresent();
        
        boolean activeMember = members.findById(info.memberId())
                .filter(member -> !member.isWithdrawn()) // 탈퇴한 회원이 아닌가
                .isPresent();

        if (!activeSession || !activeMember) {
            throw new JwtException("사용할 수 없는 로그인 세션입니다.");
        }
        return info;
    }
}
