package org.sopt.post.domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(name = "post_id", nullable = false, updatable = false)
    private Long postId;

    private String author;

    private String content;

    private int likes;

    public Comment(Long postId, String author, String content) {
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("게시글 id가 유효하지 않습니다.");
        }

        validateAuthorAndContent(author, content);

        this.postId = postId;
        this.author = author;
        this.content = content;
        this.likes = 0;
    }
    public Comment() {}

    public long getCommentId() {
        return commentId;
    }

    public long getPostId() {
        return postId;
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
