package org.sopt.post.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
//    Post save(Post post);
//    List<Post> findAll();
//    Optional<Post> findById(long id);
//    void delete(Post post);
}
