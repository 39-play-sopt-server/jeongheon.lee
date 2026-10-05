package org.sopt.post.application;

public class PostIdGenerator {
    private long generateId;

    public PostIdGenerator() {
        this.generateId = 1;
    }

    public long nextId() {
        return generateId++;
    }
}
