package org.sopt.member.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.common.presentation.code.ResponseCode;
import org.sopt.common.presentation.response.ApiResponse;
import org.sopt.member.application.MemberService;
import org.sopt.member.presentation.dto.CreateMemberRequest;
import org.sopt.member.presentation.dto.CreateMemberResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<Long> myId(
            @AuthenticationPrincipal // SecurityContext에 저장된 Authentication의 Principal을 꺼내줌
            Long memberId) {
        return ResponseEntity.ok(memberId);
    }

    // 회원가입
    @PostMapping
    public ResponseEntity<ApiResponse<CreateMemberResponse>> createMember(
            @Valid @RequestBody CreateMemberRequest request
    ) {
        Long memberId = memberService.signUp(
                request.name(), request.memberPhoneNumber(), request.memberEmail(), request.memberPassword()
        );

        CreateMemberResponse response = new CreateMemberResponse(memberId);
        ResponseCode code = ResponseCode.MEMBER_CREATED;

        return ResponseEntity.status(code.status()).body(ApiResponse.of(code, response));
    }

    // 회원탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteMember(
            @AuthenticationPrincipal Long memberId
    ) {
        memberService.deleteMember(memberId);
        ResponseCode code = ResponseCode.MEMBER_DELETED;

        return ResponseEntity.status(code.status()).body(ApiResponse.of(code, null));
    }

}
