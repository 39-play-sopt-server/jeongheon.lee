package org.sopt.post.application;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class PostIdGenerator {
    // ID 생성기를 단독으로 사용해도 중복 발급을 방지하도록 책임을 명확하게 하기 위해서 AtomicLong사용
    private final AtomicLong sequence = new AtomicLong(1L);

    public long nextId() {
        return sequence.getAndIncrement();
    }
}
