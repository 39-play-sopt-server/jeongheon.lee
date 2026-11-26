package org.sopt.auth.presentation.dto;

import java.time.Instant;

public record LoginResponse(String accessToken, Instant accessExpiresAt) {}
