package org.sopt.post.presentation.dto;

import org.sopt.post.domain.Category;

public record CreatePostRequest(
        Long memberId, String title, String content, Category category
) {}
