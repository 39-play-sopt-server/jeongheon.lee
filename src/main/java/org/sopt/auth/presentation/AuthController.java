package org.sopt.auth.presentation;

import org.sopt.auth.application.LoginService;
import org.sopt.auth.application.dto.LoginRequest;
import org.sopt.auth.application.dto.LoginResult;
import org.sopt.auth.presentation.dto.LoginResponse;
import org.sopt.common.presentation.code.ResponseCode;
import org.sopt.common.presentation.response.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final LoginService loginService;
    private final boolean secureCookie;

    public AuthController(LoginService loginService,
                          @Value("${app.auth.refresh-cookie-secure:true}") boolean secureCookie) {
        this.loginService = loginService;
        this.secureCookie = secureCookie;
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest request
    ) {
        LoginResult tokens = loginService.login(request.email(), request.password());
        ResponseCode code = ResponseCode.LOGIN_FINISHED;
        long cookieSeconds = Math.max(0, java.time.Duration.between(
                java.time.Instant.now(), tokens.refreshExpiresAt()).toSeconds());
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
