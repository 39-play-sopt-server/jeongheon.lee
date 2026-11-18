package org.sopt.member.presentation;

import jakarta.validation.Valid;
import org.sopt.common.presentation.code.ResponseCode;
import org.sopt.common.presentation.response.ApiResponse;
import org.sopt.member.application.MemberService;
import org.sopt.member.presentation.dto.CreateMemberRequest;
import org.sopt.member.presentation.dto.CreateMemberResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

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

    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(
            @PathVariable long memberId
    ) {
        memberService.deleteMember(memberId);
        ResponseCode code = ResponseCode.MEMBER_DELETED;

        return ResponseEntity.status(code.status()).body(ApiResponse.of(code, null));
    }

}
