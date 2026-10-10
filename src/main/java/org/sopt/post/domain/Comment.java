package org.sopt.post.domain;

import java.util.List;

public class Comment {
    private String author;
    private String content;
    private int likes;

    public Comment(String author, String content, int likes) {
        validateAuthorAndContent(author, content);
        this.author = author;
        this.content = content;
        this.likes = 0;
    }

    public String getAuthor() {
        return author;
    }
    public String getContent() {
        return content;
    }
    public int getLikes() {
        return likes;
    }

    private void validateAuthorAndContent(String author, String content) {
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("작성자는 비어 있을 수 없습니다.");
        }

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("본문은 비어 있을 수 없습니다.");
        }
    }

}
