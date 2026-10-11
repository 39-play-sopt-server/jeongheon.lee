package org.sopt.post.infrastructure;

import org.sopt.post.domain.Post;
import org.sopt.post.domain.PostRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryPostRepository implements PostRepository {
    private final HashMap<Long, Post> posts = new HashMap<>();

    @Override
    public void save(Post post) {
        posts.put(post.getPostId(), post);
    }

    @Override
    public List<Post> findAll() {
        // 복사한 목록을 반환해서 외부에서 목록에 직접 add()나 remove()를 할 수 없게함
        return List.copyOf(posts.values());
    }

    @Override
    public Optional<Post> findById(long id) {
        return Optional.ofNullable(posts.get(id));
    }

    @Override
    public void delete(Post post) {
        posts.remove(post.getPostId());
    }
}