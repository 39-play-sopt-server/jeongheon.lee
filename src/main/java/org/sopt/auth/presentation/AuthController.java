package org.sopt.auth.presentation;

import org.sopt.auth.application.LoginService;
import org.sopt.auth.application.RefreshService;
import org.sopt.auth.application.LogoutService;
import org.sopt.auth.application.dto.LoginRequest;
import org.sopt.auth.application.dto.LoginResult;
import org.sopt.auth.presentation.dto.LoginResponse;
import org.sopt.common.presentation.code.ResponseCode;
import org.sopt.common.presentation.response.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final LoginService loginService;
    private final RefreshService refreshService;
    private final LogoutService logoutService;
    private final boolean secureCookie;

    public AuthController(LoginService loginService, RefreshService refreshService, LogoutService logoutService,
                          @Value("${app.auth.refresh-cookie-secure:true}") boolean secureCookie) {
        this.loginService = loginService;
        this.refreshService = refreshService;
        this.logoutService = logoutService;
        this.secureCookie = secureCookie;
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest request
    ) {
        LoginResult tokens = loginService.login(request.email(), request.password());
        return tokenResponse(tokens, ResponseCode.LOGIN_FINISHED);
    }

    // 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        return tokenResponse(refreshService.refresh(refreshToken), ResponseCode.TOKEN_REFRESHED);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(Authentication authentication) {
        // 세션 폐기
        logoutService.logout(
                (Long) authentication.getPrincipal(), // 회원 ID
                (Long) authentication.getDetails() // 로그인 세션 ID
        );

        // 기존 쿠키와 같은 이름, 경로로 쿠키를 만들면서 쿠키를 즉시 만료시킬 쿠키를 만듦
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true).secure(secureCookie).sameSite("Lax")
                .path("/api/v1/auth")
                .maxAge(0) // 즉시 만료!!
                .build();

        ResponseCode code = ResponseCode.LOGOUT_FINISHED;

        return ResponseEntity.status(code.status())
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.of(code, null));
    }

    private ResponseEntity<ApiResponse<LoginResponse>> tokenResponse(LoginResult tokens, ResponseCode code) {
        // 쿠키의 유지 기간 설정
        long cookieSeconds = Math.max(
                0,
                java.time.Duration.between(java.time.Instant.now(), tokens.refreshExpiresAt()).toSeconds());

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokens.refreshToken())
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Lax")
                .path("/api/v1/auth")
                .maxAge(cookieSeconds)
                .build();

        LoginResponse response = new LoginResponse(tokens.accessToken(), tokens.accessExpiresAt());

        return ResponseEntity.status(code.status())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.of(code, response));
    }
}
