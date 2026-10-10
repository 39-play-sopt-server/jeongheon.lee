package org.sopt.post.presentation;

import org.sopt.common.ApiResponse;
import org.sopt.post.application.PostService;
import org.sopt.post.application.exception.PostNotFoundException;
import org.sopt.post.domain.Category;
import org.sopt.post.domain.Post;
import org.sopt.post.presentation.dto.CreatePostRequest;
import org.sopt.post.presentation.dto.PostResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


// 메뉴에 따라 입력받고 서비스를 호출한 뒤 결과·예외를 안내해주는 역할
@RestController
@RequestMapping(path = "/api/v1/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 게시글 작성
    @PostMapping
    public ApiResponse<PostResponse> createPost(
            @RequestBody CreatePostRequest request
    ) {
        Post post = postService.createPost(
                request.title(),
                request.content(),
                request.category()
        );
        return new ApiResponse<>("생성완료", PostResponse.from(post) );
    }

    // 게시글 목록 조회
    @GetMapping
    public PostResponse getPosts() {
        List<Post> posts = postService.getPosts();
    }
    
    // 게시글 단건 조회
    public void getPost() {
        Post post = postService.getPost(postId);
    }

    // 게시글 수정 메서드. 제목과 본문을 수정할 수 있다.
    public void updatePost() {
        long postId = postView.readPostNumber();
        // 수정할 값을 묻기 전에 게시글 존재 여부 먼저 확인
        postService.getPost(postId);
        postService.updatePost(postId, title, content);
    }

    //게시글 삭제
    public void deletePost() {
        postService.deletePost(postId);
    }
}

