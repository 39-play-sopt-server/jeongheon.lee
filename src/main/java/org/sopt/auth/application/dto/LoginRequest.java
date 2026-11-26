package org.sopt.auth.application.dto;

public record LoginRequest (
        String email, String password
)
{}
