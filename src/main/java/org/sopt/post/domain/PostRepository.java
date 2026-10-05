package org.sopt.post.domain;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    void save(Post post);
    List<Post> findAll();
    Optional<Post> findById(long id);
    void delete(Post post);
}
