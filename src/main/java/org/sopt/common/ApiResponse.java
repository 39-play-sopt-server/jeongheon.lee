package org.sopt.common;

public record ApiResponse<T>(
        String message,
        T data
) {
}