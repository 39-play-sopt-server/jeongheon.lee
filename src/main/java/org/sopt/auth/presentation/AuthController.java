package org.sopt.auth.presentation;

import lombok.RequiredArgsConstructor;
import org.sopt.auth.application.LoginService;
import org.sopt.auth.application.dto.LoginRequest;
import org.sopt.auth.application.dto.LoginResult;
import org.sopt.common.presentation.code.ResponseCode;
import org.sopt.common.presentation.response.ApiResponse;
import org.sopt.member.application.MemberService;
import org.sopt.member.domain.MemberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResult>> login(
            @RequestBody LoginRequest request
    ) {
        LoginResult tokens = loginService.login(request.email(), request.password());
        ResponseCode code = ResponseCode.LOGIN_FINISHED;

        return ResponseEntity.status(code.status()).body(ApiResponse.of(code, tokens));
    }
}
