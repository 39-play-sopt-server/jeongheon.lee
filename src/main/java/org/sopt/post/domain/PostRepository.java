package org.sopt.post.domain;

import java.util.List;

public interface PostRepository {
    void save(Post post);
    List<Post> findAll();
    void delete(Post post);
}
