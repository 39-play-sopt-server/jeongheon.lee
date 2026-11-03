package org.sopt.post.presentation.dto;

import org.sopt.post.application.dto.PostResult;
import org.springframework.data.domain.Page;

import java.util.List;

// 게시글 목록과 페이지 정보를 함께 전달하기 위한 응답 DTO
public record PostPageResponse(
        List<PostResponse> content, // 현재 페이지에 포함된 게시글 목록
        int page, // 현재 페이지 번호 (0부터 시작)
        int size, // 요청한 한 페이지의 최대 게시글 개수
        long totalElements, // 전체 게시글 개수
        int totalPages, // 전체 페이지 개수
        boolean hasNext // 다음 페이지가 있는지 여부
) {
    // Page에서 게시글 목록과 페이지 정보를 꺼내 응답 형식으로 변환
    public static PostPageResponse from(Page<PostResult> posts) {
        return new PostPageResponse(
                // getContent()로 현재 페이지의 목록만 꺼낸 뒤, 각 게시글을 PostResponse로 변환
                posts.getContent().stream().map(PostResponse::from).toList(),
                posts.getNumber(),
                posts.getSize(),
                posts.getTotalElements(),
                posts.getTotalPages(),
                posts.hasNext()
        );
    }
}
