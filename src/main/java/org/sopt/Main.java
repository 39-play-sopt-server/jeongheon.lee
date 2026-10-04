package org.sopt;


import org.sopt.post.presentation.PostController;

public class Main {

    public static void main(String[] args) {
        PostController postController = new PostController();

        postController.run();
    }
}