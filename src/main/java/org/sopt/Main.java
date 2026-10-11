package org.sopt;


import org.sopt.post.application.PostIdGenerator;
import org.sopt.post.application.PostService;
import org.sopt.post.domain.PostRepository;
import org.sopt.post.infrastructure.InMemoryPostRepository;
import org.sopt.post.presentation.PostController;
import org.sopt.post.presentation.PostView;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
//        PostRepository postRepository = new InMemoryPostRepository();
//        PostIdGenerator postIdGenerator = new PostIdGenerator();
//
//        PostService postService = new PostService(postRepository, postIdGenerator);
//        PostView postView = new PostView();
//
//        PostController postController = new PostController(postView, postService);
//
//        postController.run();
          SpringApplication.run(Main.class, args);

    }
}