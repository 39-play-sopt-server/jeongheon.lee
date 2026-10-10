package org.sopt.post.presentation.dto;

import org.sopt.post.domain.Category;

public record CreatePostRequest(
        String title, String content, Category category
) {}
