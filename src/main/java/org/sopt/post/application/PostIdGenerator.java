package org.sopt.post.application;

import org.springframework.stereotype.Component;

@Component
public class PostIdGenerator {
    private long generateId;

    public PostIdGenerator() {
        this.generateId = 1;
    }

    public long nextId() {
        return generateId++;
    }
}
