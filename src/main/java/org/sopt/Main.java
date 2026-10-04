package org.sopt;


import org.sopt.post.application.PostService;
import org.sopt.post.domain.PostRepository;
import org.sopt.post.infrastructure.InMemoryPostRepository;
import org.sopt.post.presentation.PostController;
import org.sopt.post.presentation.PostView;

public class Main {

    public static void main(String[] args) {
        PostRepository postRepository = new InMemoryPostRepository();
        PostService postService = new PostService(postRepository);
        PostView postView = new PostView();

        PostController postController = new PostController(postView, postService);

        postController.run();
    }
}