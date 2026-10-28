package org.sopt.post.infrastructure;

import org.sopt.post.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostId(long postId);
}
