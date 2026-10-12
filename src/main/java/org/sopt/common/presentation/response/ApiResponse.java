package org.sopt.common.presentation.response;

import org.sopt.common.presentation.code.ResponseCode;

// 성공과 실패 모두 같은 네 필드를 사용하도록 정의
// 실패하거나 반환할 데이터가 없으면 result는 null
public record ApiResponse<T>(
        boolean isSuccess,
        String code,
        String message,
        T result
) { //of()는 enum에서 코드·메시지를 가져와 공통 응답을 만드는 메서드
    public static <T> ApiResponse<T> of(ResponseCode code, T result) {
        return new ApiResponse<>(
                code.status().is2xxSuccessful(),
                code.code(),
                code.message(),
                result
        );
    }
}