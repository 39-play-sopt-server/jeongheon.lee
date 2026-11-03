package org.sopt;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
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