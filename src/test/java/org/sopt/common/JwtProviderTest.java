package org.sopt.common;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.sopt.auth.infrastructure.JwtAuthenticationFilter;
import org.sopt.auth.application.AccessAuthenticationService;
import org.sopt.auth.domain.AuthSessionRepository;
import org.sopt.member.domain.MemberRepository;
import org.sopt.auth.domain.AuthSession;
import org.sopt.member.domain.Member;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;

class JwtProviderTest {
    private final byte[] key = "01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8);
    private final JwtProvider provider = new JwtProvider(Base64.getEncoder().encodeToString(key), 1800000L, 1209600000L);

    @Test void readsBothTokenTypesAndGeneratesDistinctIds() {
        String access = provider.createAccessToken(1L, 2L);
        String refresh = provider.createRefreshToken(1L, 2L);
        var accessInfo = provider.parseAccessToken(access);
        var refreshInfo = provider.parseRefreshToken(refresh);
        assertEquals(1L, accessInfo.memberId());
        assertEquals(2L, accessInfo.sessionId());
        assertEquals(2L, refreshInfo.sessionId());
        assertTrue(refreshInfo.expiresAt().isAfter(accessInfo.expiresAt()));
        assertNotEquals(refreshInfo.tokenId(), provider.parseRefreshToken(provider.createRefreshToken(1L, 2L)).tokenId());
    }

    @Test void rejectsWrongUseTamperingAndExpiredTokens() {
        assertThrows(JwtException.class, () -> provider.parseAccessToken(provider.createRefreshToken(1L, 2L)));
        assertThrows(JwtException.class, () -> provider.parseRefreshToken(provider.createAccessToken(1L, 2L)));
        String token = provider.createAccessToken(1L, 2L);
        String[] parts = token.split("\\.");
        String payload = Base64.getUrlEncoder().withoutPadding().encodeToString("{\"sub\":\"999\"}".getBytes(StandardCharsets.UTF_8));
        assertThrows(JwtException.class, () -> provider.parseAccessToken(parts[0] + "." + payload + "." + parts[2]));
        String expired = Jwts.builder().subject("1").claim("sid", "2").claim("token_use", "access")
                .id("old").expiration(Date.from(Instant.now().minusSeconds(60))).signWith(Keys.hmacShaKeyFor(key)).compact();
        assertThrows(JwtException.class, () -> provider.parseAccessToken(expired));
    }

    @Test void filterAuthenticatesOnlyAccessTokens() throws Exception {
        var sessions = mock(AuthSessionRepository.class);
        var members = mock(MemberRepository.class);
        var member = mock(Member.class);
        when(members.findById(1L)).thenReturn(Optional.of(member));
        when(sessions.findById(2L)).thenReturn(Optional.of(new AuthSession(1L, "hash", Instant.now().plusSeconds(3600), Instant.now())));
        var filter = new JwtAuthenticationFilter(new AccessAuthenticationService(provider, sessions, members));
        try {
            var refreshRequest = new MockHttpServletRequest();
            refreshRequest.addHeader("Authorization", "Bearer " + provider.createRefreshToken(1L, 2L));
            filter.doFilter(refreshRequest, new MockHttpServletResponse(), new MockFilterChain());
            assertNull(SecurityContextHolder.getContext().getAuthentication());
            var accessRequest = new MockHttpServletRequest();
            accessRequest.addHeader("Authorization", "Bearer " + provider.createAccessToken(1L, 2L));
            filter.doFilter(accessRequest, new MockHttpServletResponse(), new MockFilterChain());
            assertEquals(1L, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
