package org.sopt.common.presentation.exception;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.sopt.common.presentation.code.ResponseCode;
import org.sopt.common.presentation.response.ApiResponse;
import org.sopt.post.application.exception.PostNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice // 여러 Controller의 예외를 한곳에서 처리할 수 있게 함
//요청 처리 중 발생한 예외를 HTTP 상태와 공통 JSON 응답으로 변환하는 역할을 담당
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // 없는 게시글을 404로 변환해서 공통 응답처리
    @ExceptionHandler(PostNotFoundException.class) // 이 예외를 처리할 메서드라고 지정
    public ResponseEntity<ApiResponse<Void>> handlePostNotFound(
            PostNotFoundException exception // 실제로 발생한 예외 객체를 받음
    ) {
        return failure(ResponseCode.POST_NOT_FOUND); // 선택한 오류 코드로 HTTP 응답을 만드는 보조 메서드를 호출
    }

    // Post 애그리거트에 있는 "제목은 비어 있을 수 없다"라는 도메인 규칙 검증 실패를 400으로 변환
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidInput(
            IllegalArgumentException exception
    ) {
        return failure(ResponseCode.INVALID_REQUEST);
    }

    // 나머지 예외를 500으로 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(
            Exception exception
    ) {
        logger.error("예상하지 못한 오류", exception);
        return failure(ResponseCode.SERVER_ERROR);
    }

    @Override
    @NullMarked // 이 메서드는 기본적으로 null 허용 안함
    protected @Nullable ResponseEntity<Object> handleExceptionInternal(
            Exception exception,
            @Nullable Object body, //하지만 body에는 null이 전달될 수 있음
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        ResponseCode code = status.is5xxServerError()
                ? ResponseCode.SERVER_ERROR
                : ResponseCode.INVALID_REQUEST;

        return super.handleExceptionInternal(
                exception,
                ApiResponse.of(code, null),
                headers,
                status,
                request
        );
    }

    private ResponseEntity<ApiResponse<Void>> failure(ResponseCode code) {
        return ResponseEntity.status(code.status())
                .body(ApiResponse.<Void>of(code, null));
    }
}