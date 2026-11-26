package org.sopt.auth.application;

import lombok.RequiredArgsConstructor;
import org.sopt.auth.application.exception.InvalidRefreshTokenException;
import org.sopt.auth.domain.AuthSession;
import org.sopt.auth.domain.AuthSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LogoutService {
    private final AuthSessionRepository sessions;

    @Transactional
    public void logout(Long memberId, Long sessionId) {
        AuthSession session = sessions.findByIdForUpdate(sessionId)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (!memberId.equals(session.getMemberId()) || !session.isUsable(Instant.now())) {
            throw new InvalidRefreshTokenException();
        }
        session.revoke(Instant.now()); // 세션 폐기
    }
}
