package org.sopt.common.presentation.code;

import org.springframework.http.HttpStatus;

public enum ResponseCode {
    POST_CREATED(HttpStatus.CREATED, "POST_CREATED", "게시글 생성 성공"),
    POST_FOUND(HttpStatus.OK, "POST_FOUND", "게시글 조회 성공"),
    POST_LIST_FOUND(HttpStatus.OK, "POST_LIST_FOUND", "게시글 목록 조회 성공"),
    POST_UPDATED(HttpStatus.OK, "POST_UPDATED", "게시글 수정 성공"),
    POST_DELETED(HttpStatus.OK, "POST_DELETED", "게시글 삭제 성공"),

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_NOT_FOUND", "게시글을 찾을 수 없습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "요청이 올바르지 않습니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR", "서버 오류가 발생했습니다."),

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다"),
    MEMBER_CREATED(HttpStatus.CREATED, "MEMBER_CREATED", "회원가입 성공"),
    MEMBER_DELETED(HttpStatus.OK, "MEMBER_DELETED", "회원 삭제 성공");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ResponseCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus status() {
        return status;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }
}