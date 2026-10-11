package org.sopt.post.presentation.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.common.presentation.response.ApiResponse;
import org.sopt.post.presentation.dto.CreatePostRequest;
import org.sopt.post.presentation.dto.PostResponse;
import org.sopt.post.presentation.dto.UpdatePostRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "게시글", description = "게시글 생성·조회·수정·삭제 API")
public interface PostApi {

    @Operation(summary = "게시글 생성")
    ResponseEntity<ApiResponse<PostResponse>> createPost(
            CreatePostRequest request
    );

    @Operation(summary = "게시글 목록 조회")
    ResponseEntity<ApiResponse<List<PostResponse>>> getPosts();

    @Operation(summary = "게시글 상세 조회", description = "조회에 성공하면 조회수가 1 증가합니다.")
    ResponseEntity<ApiResponse<PostResponse>> getPost(
            @Parameter(description = "게시글 ID", example = "1")
            long postId
    );

    @Operation(summary = "게시글 수정", description = "제목과 본문을 수정합니다.")
    ResponseEntity<ApiResponse<Void>> updatePost(
            long postId,
            UpdatePostRequest request
    );

    @Operation(summary = "게시글 삭제")
    ResponseEntity<ApiResponse<Void>> deletePost(
            long postId
    );
}
