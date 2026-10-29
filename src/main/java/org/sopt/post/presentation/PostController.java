package org.sopt.post.presentation;

import org.sopt.common.presentation.code.ResponseCode;
import org.sopt.common.presentation.response.ApiResponse;
import org.sopt.post.application.PostService;
import org.sopt.post.application.dto.PostResult;
import org.sopt.post.presentation.docs.PostApi;
import org.sopt.post.presentation.dto.CreatePostRequest;
import org.sopt.post.presentation.dto.PostPageResponse;
import org.sopt.post.presentation.dto.PostResponse;
import org.sopt.post.presentation.dto.UpdatePostRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 메뉴에 따라 입력받고 서비스를 호출한 뒤 결과·예외를 안내해주는 역할
@RestController
@RequestMapping("/api/v1/posts")
public class PostController implements PostApi {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 게시글 작성
    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @RequestBody CreatePostRequest request
    ) {
        // 게시글 생성
        PostResult post = postService.createPost(
                request.memberId(),
                request.title(),
                request.content(),
                request.category()
        );
        // 응답 DTO 변환
        PostResponse response = PostResponse.from(post);

        // 응답 코드 선택
        ResponseCode code = ResponseCode.POST_CREATED;

        // HTTP 상태 및 공통 응답 반환
        return ResponseEntity.status(code.status())
                .body(ApiResponse.of(code, response));
    }

    // 게시글 목록 조회. URL의 ?page=0&size=10 같은 쿼리 파라미터로 조회할 범위를 전달받음
    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<PostPageResponse>> getPosts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        // 서비스가 반환한 게시글과 페이지 정보를 API 응답 DTO로 변환
        PostPageResponse response = PostPageResponse.from(postService.getPosts(page, size));

        ResponseCode code = ResponseCode.POST_LIST_FOUND;

        return ResponseEntity.status(code.status())
                .body(ApiResponse.of(code, response));
    }
    
    // 게시글 단건 조회
    @Override
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(
            @PathVariable("postId") long postId
    ) {
        PostResult post = postService.getPostDetail(postId);

        ResponseCode code = ResponseCode.POST_FOUND;
        PostResponse response = PostResponse.from(post);

        return ResponseEntity.status(code.status())
                .body(ApiResponse.of(code, response));

    }

    // 게시글 수정 메서드. 제목과 본문을 수정할 수 있다.
    @Override
    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePost(
            @PathVariable("postId") long postId,
            @RequestBody UpdatePostRequest request
    ) {
        postService.updatePost(postId, request.title(), request.content());

        ResponseCode code = ResponseCode.POST_UPDATED;

        return ResponseEntity.status(code.status())
                .body(ApiResponse.of(code, null));
    }

    //게시글 삭제
    @Override
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable("postId") long postId
    ) {
        // 삭제 권한 확인을 해야하는데... 회원 기능이 없으므로... 패쓰
        postService.deletePost(postId);

        ResponseCode code = ResponseCode.POST_DELETED;
        return ResponseEntity.status(code.status())
                .body(ApiResponse.of(code, null));
    }
}

