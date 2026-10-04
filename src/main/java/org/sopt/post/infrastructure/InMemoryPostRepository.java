package org.sopt.post.infrastructure;

import org.sopt.post.domain.Post;
import org.sopt.post.domain.PostRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryPostRepository implements PostRepository {
    private final List<Post> posts = new ArrayList<>();

    @Override
    public void save(Post post) {
        posts.add(post);
    }

    @Override
    public List<Post> findAll() {
        // 복사한 목록을 반환해서 외부에서 목록에 직접 add()나 remove()를 할 수 없게함
        return List.copyOf(posts);
    }

    @Override
    public void delete(Post post) {
        posts.remove(post);
    }
}