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

    // 같은 세션을 갱신하는 요청은 트랜잭션이 끝날 때까지 순서대로 처리한다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from AuthSession s where s.sessionId = :sessionId")
    Optional<AuthSession> findByIdForUpdate(@Param("sessionId") Long sessionId);
}
