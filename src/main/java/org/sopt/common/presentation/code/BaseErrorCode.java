package org.sopt.common.presentation.code;

import org.springframework.http.HttpStatusCode;

public interface BaseErrorCode {
    HttpStatusCode getHttpStatus();
    String getCode();
    String getMessage();
}