package org.sopt.auth;

import org.junit.jupiter.api.Test;
import org.sopt.Main;
import org.sopt.auth.application.RefreshService;
import org.sopt.auth.application.dto.LoginResult;
import org.sopt.auth.application.exception.InvalidRefreshTokenException;
import org.sopt.auth.domain.AuthSession;
import org.sopt.auth.domain.AuthSessionRepository;
import org.sopt.common.JwtProvider;
import org.sopt.member.domain.Member;
import org.sopt.member.domain.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import jakarta.servlet.Filter;
import jakarta.servlet.http.Cookie;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Main.class, properties = {
        "spring.datasource.url=jdbc:h2:mem:refresh_test;MODE=MySQL;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa", "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect", "spring.jpa.hibernate.ddl-auto=create-drop",
        "jwt.secret=MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE="
})
class RefreshServiceIntegrationTest {
    @Autowired MemberRepository members;
    @Autowired AuthSessionRepository sessions;
    @Autowired JwtProvider jwt;
    @Autowired RefreshService service;
    @Autowired TransactionTemplate transactions;
    @Autowired WebApplicationContext context;

    @Test void concurrentUseOfSameTokenAllowsOnlyOneRotation() throws Exception {
        String oldToken = transactions.execute(status -> {
            Member member = members.save(Member.signUp("회원", "01012345678", "refresh@example.com", "hash"));
            Instant now = Instant.now();
            AuthSession session = sessions.save(new AuthSession(member.getMemberId(), "pending", now.plusSeconds(60), now));
            String token = jwt.createRefreshToken(member.getMemberId(), session.getSessionId());
            try {
                String hash = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8)));
                session.rotateToken(hash, jwt.parseRefreshToken(token).expiresAt(), now);
            } catch (Exception e) { throw new IllegalStateException(e); }
            return token;
        });
        CountDownLatch start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            var first = executor.submit(() -> { start.await(); try { return service.refresh(oldToken); } catch (InvalidRefreshTokenException e) { return null; } });
            var second = executor.submit(() -> { start.await(); try { return service.refresh(oldToken); } catch (InvalidRefreshTokenException e) { return null; } });
            start.countDown();
            LoginResult a = first.get(15, TimeUnit.SECONDS);
            LoginResult b = second.get(15, TimeUnit.SECONDS);
            assertTrue((a == null) != (b == null), "동일 토큰은 한 번만 갱신되어야 한다");
            LoginResult issued = a != null ? a : b;
            assertThrows(InvalidRefreshTokenException.class, () -> service.refresh(oldToken));
            assertNotNull(service.refresh(issued.refreshToken()).accessToken());
        }
    }

    @Test void endpointAcceptsRefreshCookieWithExpiredAccessAndRejectsReuse() throws Exception {
        String token = transactions.execute(status -> {
            Member member = members.save(Member.signUp("회원", "01012345678", "endpoint@example.com", "hash"));
            Instant now = Instant.now();
            AuthSession session = sessions.save(new AuthSession(member.getMemberId(), "pending", now.plusSeconds(60), now));
            String refresh = jwt.createRefreshToken(member.getMemberId(), session.getSessionId());
            try {
                String hash = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(refresh.getBytes(StandardCharsets.UTF_8)));
                session.rotateToken(hash, jwt.parseRefreshToken(refresh).expiresAt(), now);
            } catch (Exception e) { throw new IllegalStateException(e); }
            return refresh;
        });
        String expiredAccess = Jwts.builder().subject("1").claim("sid", "1").claim("token_use", "access")
                .id("old").expiration(Date.from(Instant.now().minusSeconds(60)))
                .signWith(Keys.hmacShaKeyFor("01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8))).compact();
        var mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(context.getBean("springSecurityFilterChain", Filter.class)).build();
        mvc.perform(post("/api/v1/auth/refresh").cookie(new Cookie("refreshToken", token))
                .header("Authorization", "Bearer " + expiredAccess))
                .andExpect(status().isOk()).andExpect(header().exists("Set-Cookie"))
                .andExpect(jsonPath("$.result.accessToken").isNotEmpty());
        mvc.perform(post("/api/v1/auth/refresh").cookie(new Cookie("refreshToken", token)))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/v1/auth/refresh")).andExpect(status().isUnauthorized());
    }

    @Test void logoutRevokesOnlyCurrentSessionAndClearsCookie() throws Exception {
        Long[] ids = transactions.execute(status -> {
            Member member = members.save(Member.signUp("회원", "01012345678", "logout@example.com", "hash"));
            Instant now = Instant.now();
            AuthSession first = sessions.save(new AuthSession(member.getMemberId(), "hash", now.plusSeconds(3600), now));
            AuthSession second = sessions.save(new AuthSession(member.getMemberId(), "hash", now.plusSeconds(3600), now));
            return new Long[]{member.getMemberId(), first.getSessionId(), second.getSessionId()};
        });
        String access = jwt.createAccessToken(ids[0], ids[1]);
        String otherAccess = jwt.createAccessToken(ids[0], ids[2]);
        var mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(context.getBean("springSecurityFilterChain", Filter.class)).build();
        mvc.perform(get("/api/v1/members/me").header("Authorization", "Bearer " + access))
                .andExpect(status().isOk());
        mvc.perform(post("/api/v1/auth/logout").header("Authorization", "Bearer " + access))
                .andExpect(status().isOk()).andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("Max-Age=0")))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("Path=/api/v1/auth")));
        mvc.perform(get("/api/v1/members/me").header("Authorization", "Bearer " + access))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/members/me").header("Authorization", "Bearer " + otherAccess))
                .andExpect(status().isOk());
        mvc.perform(post("/api/v1/auth/logout")).andExpect(status().isUnauthorized());
        assertTrue(sessions.findById(ids[1]).orElseThrow().isRevoked());
        assertThrows(InvalidRefreshTokenException.class, () -> service.refresh(jwt.createRefreshToken(ids[0], ids[1])));
    }
}
