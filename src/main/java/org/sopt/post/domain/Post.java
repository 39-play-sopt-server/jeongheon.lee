package org.sopt.post.domain;

import java.util.ArrayList;
import java.util.List;


// 제목·본문·카테고리 규칙 보장, 자신의 상태 변경
public class Post {
    private final Category category; // 카테고리는 수정 불가하다.
    private String title;
    private String content;
    private int likes;
    private final List<Comment> comments = new ArrayList<>();

    public Post(String title, String content, Category category) {
        validateTitleAndContent(title, content);
        validateCategory(category);

        this.category = category;
        this.title = title;
        this.content = content;
        this.likes = 0;

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

    public int getLikes() {
        return likes;
    }

    // 원본 List객체를 반환하지 않도록 copy해서 반환함
    public List<Comment> getComments() {
        return List.copyOf(comments);
    }

    // 댓글 추가 기능
    public void addComment(Comment comment) {
        // 아무 내용 없는 댓글은 작성 불가
        if (comment == null) {
            throw new IllegalArgumentException("댓글이 필요합니다.");
        }

        comments.add(comment);
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