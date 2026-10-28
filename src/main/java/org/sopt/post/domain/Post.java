package org.sopt.post.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


// 제목·본문·카테고리 규칙 보장, 자신의 상태 변경
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long postId;

    @Enumerated(EnumType.STRING)
    private Category category; // 카테고리는 수정 불가하다.

    private String title;

    private String content;

    private long viewCount = 0L;

    protected Post() {}

    public Post(String title, String content, Category category) {
        validateTitleAndContent(title, content);
        validateCategory(category);

        this.category = category;
        this.title = title;
        this.content = content;

    }
    public long getPostId() {
        return postId;
    }
    public Category getCategory() {
        return category;
    }
    // 게시글 조회 시 제목 전달
    public String getTitle() {
        return title;
    }
    // 게시글 조회 시 본문 전달
    public String getContent() {
        return content;
    }

    public long getViewCount() {
        return viewCount;
    }

    // 조회수 증가
    public void increaseViewCount() {
        viewCount++;
    }

    // 게시물을 업데이트한다.
    // 게시물을 생성할 때와 똑같이 제목 or 본문이 null이거나, 공백만 있으면 예외를 발생시킨다.
    public void update(String title, String content) {
        validateTitleAndContent(title, content);

        this.title = title;
        this.content = content;
    }

    // "제목과 본문이 비어있는 경우에는 게시글이 작성되지 않는다." 라는 도메인 규칙을 구현한 메서드
    // 제목 or 본문이 null이거나, 공백만 있으면 예외를 발생시킨다.
    private void validateTitleAndContent(String title, String content) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("제목은 비어 있을 수 없습니다.");
        }

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("본문은 비어 있을 수 없습니다.");
        }
    }
    
    // 카테고리가 선택되었는지 검증하는 메서드
    private void validateCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("카테고리는 반드시 선택해야 합니다.");
        }
    }
}