package org.sopt.post.presentation.dto;

import org.sopt.post.application.dto.PostResult;
import org.sopt.post.domain.Category;

public record PostResponse(
        long id,
        String title,
        String content,
        Category category,
        long viewCount,
        String authorName,
        boolean authorWithdrawn
) {
    public static PostResponse from(PostResult result) {
        return new PostResponse(
                result.id(),
                result.title(),
                result.content(),
                result.category(),
                result.viewCount(),
                result.authorWithdrawn() ? "탈퇴한 회원" : result.authorName(),
                result.authorWithdrawn()
        );
    }
}
