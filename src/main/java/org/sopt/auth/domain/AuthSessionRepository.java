package org.sopt.auth.domain;

import org.springframework.data.repository.Repository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface AuthSessionRepository extends Repository<AuthSession, Long> {
    AuthSession save(AuthSession session);

    Optional<AuthSession> findById(Long sessionId);

    // 두 요청이 같은 Refresh Token으로 동시에 들어오면 PESSIMISTIC_WRITE 로 처리
    // -> RefreshService의 먼저 들어온 요청의 @Transactional이 끝날 때까지 잠금이 유지
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from AuthSession s where s.sessionId = :sessionId")
    Optional<AuthSession> findByIdForUpdate(@Param("sessionId") Long sessionId);
}
