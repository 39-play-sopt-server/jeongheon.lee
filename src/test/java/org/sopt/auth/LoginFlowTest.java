package org.sopt.auth;

import org.junit.jupiter.api.Test;
import org.sopt.auth.application.LoginService;
import org.sopt.auth.application.RefreshService;
import org.sopt.auth.application.LogoutService;
import org.sopt.auth.application.dto.LoginResult;
import org.sopt.auth.application.dto.LoginRequest;
import org.sopt.auth.domain.AuthSession;
import org.sopt.auth.domain.AuthSessionRepository;
import org.sopt.auth.presentation.AuthController;
import org.sopt.common.JwtProvider;
import org.sopt.member.domain.Member;
import org.sopt.member.domain.MemberRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoginFlowTest {
    private final MemberRepository members = mock(MemberRepository.class);
    private final AuthSessionRepository sessions = mock(AuthSessionRepository.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final JwtProvider jwt = new JwtProvider(Base64.getEncoder().encodeToString(
            "01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8)), 1800000L, 1209600000L);
    private final LoginService service = new LoginService(members, encoder, jwt, sessions);

    @Test void loginStoresRefreshHashAndReturnsBothTokensAndExpirations() throws Exception {
        Member member = mock(Member.class);
        when(member.getMemberId()).thenReturn(42L);
        when(member.getPassword()).thenReturn("password-hash");
        when(members.findByEmail("user@example.com")).thenReturn(Optional.of(member));
        when(encoder.matches("password", "password-hash")).thenReturn(true);
        when(sessions.save(any(AuthSession.class))).thenAnswer(call -> {
            AuthSession session = call.getArgument(0);
            var field = AuthSession.class.getDeclaredField("sessionId");
            field.setAccessible(true);
            field.set(session, 123L);
            return session;
        });

        LoginResult result = service.login("user@example.com", "password");
        var captured = org.mockito.ArgumentCaptor.forClass(AuthSession.class);
        verify(sessions).save(captured.capture());
        AuthSession session = captured.getValue();
        String expectedHash = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(result.refreshToken().getBytes(StandardCharsets.UTF_8)));
        assertEquals(expectedHash, session.getRefreshTokenHash());
        assertEquals(result.refreshExpiresAt(), session.getExpiresAt());
        assertEquals(123L, jwt.parseAccessToken(result.accessToken()).sessionId());
        assertEquals(123L, jwt.parseRefreshToken(result.refreshToken()).sessionId());
        assertEquals(jwt.parseAccessToken(result.accessToken()).expiresAt(), result.accessExpiresAt());
    }

    @Test void withdrawnMemberCannotCreateSession() {
        Member member = mock(Member.class);
        when(member.getPassword()).thenReturn("hash");
        when(member.isWithdrawn()).thenReturn(true);
        when(members.findByEmail("user@example.com")).thenReturn(Optional.of(member));
        when(encoder.matches("password", "hash")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> service.login("user@example.com", "password"));
        verifyNoInteractions(sessions);
    }

    @Test void controllerSendsRefreshOnlyInHttpOnlyCookie() {
        LoginService mockedService = mock(LoginService.class);
        Instant expiry = Instant.now().plusSeconds(3600);
        when(mockedService.login("email", "password"))
                .thenReturn(new LoginResult("access-value", expiry, "refresh-value", expiry));
        var response = new AuthController(mockedService, mock(RefreshService.class), mock(LogoutService.class), true).login(new LoginRequest("email", "password"));
        String cookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertNotNull(cookie);
        assertTrue(cookie.contains("refreshToken=refresh-value"));
        assertTrue(cookie.contains("HttpOnly"));
        assertTrue(cookie.contains("Secure"));
        assertTrue(cookie.contains("SameSite=Lax"));
        assertTrue(cookie.contains("Path=/api/v1/auth"));
        assertEquals("access-value", response.getBody().result().accessToken());
        assertEquals(expiry, response.getBody().result().accessExpiresAt());
    }
}
