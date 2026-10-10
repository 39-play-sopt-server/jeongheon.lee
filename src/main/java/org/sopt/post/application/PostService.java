package org.sopt.post.application;

import org.sopt.post.application.exception.PostNotFoundException;
import org.sopt.post.domain.Category;
import org.sopt.post.domain.Post;
import org.sopt.post.domain.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

// 게시글 작성·조회·수정·삭제의 순서 조율 담당
@Service
public class PostService {
    private final PostRepository repository;
    private final PostIdGenerator postIdGenerator;

    // 생성자
    public PostService(PostRepository repository, PostIdGenerator postIdGenerator) {
        this.repository = repository;
        this.postIdGenerator = postIdGenerator;
    }

    // Post를 생성하고 저장소에 보관 후 반환함
    // 빈 제목·본문 검사(도메인 규칙)는 Post가 수행함
    public Post createPost(String title, String content, Category category) {
        Post post = new Post(postIdGenerator.nextId(), title, content, category);
        repository.save(post);
        return post;
    }

    // 게시물 목록 조회
    public List<Post> getPosts() {
        return repository.findAll();
    }

    // 게시물 조회, 해당 번호의 게시글이 있는지 확인함.
    public Post getPost(long postId) {
            return repository.findById(postId).orElseThrow(()
                    -> new PostNotFoundException("그 게시물은 없어요;;"));
    }

    // 게시글을 찾고 수정 규칙의 실행을 Post(애그리거트)에 맡김
    public void updatePost(long postId, String title, String content) {
        Post post = getPost(postId);
        post.update(title, content);

    }

    // 게시글을 찾고 저장소에 제거를 요청
    public void deletePost(long postId) {
        Post post = getPost(postId);
        repository.delete(post);
    }
}