package org.sopt.post.presentation.dto;

import org.sopt.post.domain.Category;
import org.sopt.post.domain.Post;

public record PostResponse(
        long id,
        String title,
        String content,
        Category category
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                post.getCategory()
        );
    }
}
