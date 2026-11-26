package org.sopt.auth.application.dto;

import java.time.Instant;

// 서비스 내부 결과: 컨트롤러가 응답 본문과 쿠키로 나누어 전달한다.
public record LoginResult(String accessToken, Instant accessExpiresAt,
                          String refreshToken, Instant refreshExpiresAt) {}
