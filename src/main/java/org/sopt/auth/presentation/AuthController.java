package org.sopt.auth.presentation;

import lombok.RequiredArgsConstructor;
import org.sopt.auth.application.LoginService;
import org.sopt.auth.application.dto.LoginRequest;
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

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequest request) {
        String accessToken = loginService.login(request.email(), request.password());
        ResponseCode code = ResponseCode.LOGIN_FINISHED;
        return ResponseEntity.status(200).body(ApiResponse.of(code, accessToken));
    }
}
