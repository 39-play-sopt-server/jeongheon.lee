package org.sopt.post.application.dto;

import org.sopt.post.domain.Category;
import org.sopt.post.domain.Post;

// 서비스가 반환할 불변 결과 객체(서비스가 반환하는 특정 시점의 값)
public record PostResult(
        long id,
        Category category,
        String title,
        String content,
        long viewCount
) {
    public static PostResult from(Post post) {
        return new PostResult(
                post.getPostId(),
                post.getCategory(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount()
        );
    }

}
