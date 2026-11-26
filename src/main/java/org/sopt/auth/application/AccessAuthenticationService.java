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
    public JwtProvider.TokenInfo authenticate(String token) {
        JwtProvider.TokenInfo info = jwtProvider.parseAccessToken(token);
        boolean activeSession = sessions.findById(info.sessionId())
                .filter(session -> info.memberId().equals(session.getMemberId()))
                .filter(session -> session.isUsable(Instant.now()))
                .isPresent();
        boolean activeMember = members.findById(info.memberId())
                .filter(member -> !member.isWithdrawn())
                .isPresent();
        if (!activeSession || !activeMember) {
            throw new JwtException("사용할 수 없는 로그인 세션입니다.");
        }
        return info;
    }
}
