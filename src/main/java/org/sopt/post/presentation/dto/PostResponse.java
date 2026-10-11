package org.sopt.post.presentation.dto;

import org.sopt.post.application.dto.PostResult;
import org.sopt.post.domain.Category;
import org.sopt.post.domain.Post;

public record PostResponse(
        long id,
        String title,
        String content,
        Category category,
        long viewCount
) {
    public static PostResponse from(PostResult result) {
        return new PostResponse(
                result.id(),
                result.title(),
                result.content(),
                result.category(),
                result.viewCount()
        );
    }
}
