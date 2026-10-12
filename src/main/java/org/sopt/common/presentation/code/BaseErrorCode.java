package org.sopt.common.presentation.code;

import org.springframework.http.HttpStatusCode;

// 도메인별 응답 enum이 늘어날 때 리펙토링 예정
public interface BaseErrorCode {
    HttpStatusCode getHttpStatus();
    String getCode();
    String getMessage();
}