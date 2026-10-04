package org.sopt;

public class Post {
    private String title;
    private String content;

    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // 게시글 조회 시 제목 전달
    public String getTitle() {
        return title;
    }

    // 게시글 조회 시 본문 전달
    public String getContent() {
        return content;
    }

    // 게시글 수정 시 제목 전달
    public void setTitle(String title) {
        this.title = title;
    }

    // 게시글 조회 시 본문 전달
    public void setContent(String content) {
        this.content = content;
    }

}