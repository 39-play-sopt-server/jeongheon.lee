package org.sopt.post.domain;

import java.util.List;

public class Comment {
    private String author;
    private String content;
    private int likes;

    public Comment(String author, String content, int likes) {
        this.author = author;
        this.content = content;
        this.likes = likes;
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


}
