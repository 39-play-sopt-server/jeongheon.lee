package org.sopt.post.presentation;

import org.sopt.post.application.PostService;
import org.sopt.post.application.exception.PostNotFoundException;
import org.sopt.post.domain.Category;
import org.sopt.post.domain.Post;

import java.util.ArrayList;
import java.util.List;


// 메뉴에 따라 입력받고 서비스를 호출한 뒤 결과·예외를 안내해주는 역할
public class PostController {
    private final PostView postView;
    private final PostService postService;

    // 생성자
    public PostController(PostView postView, PostService postService) {
        this.postView = postView;
        this.postService = postService;
    }

    public void run() {
        while (true) {
            postView.printMenu();

            try {
                int command = postView.setCommand();// command 입력받아서 저장

                switch (command) {
                    case 1 -> post();
                    case 2 -> getPosts();
                    case 3 -> getPost();
                    case 4 -> updatePost();
                    case 5 -> deletePost();
                    case 6 -> {
                        postView.printMessage("프로그램을 종료합니다.");
                        return;
                    }
                    default -> postView.printMessage("잘못된 메뉴 번호입니다.");
                }
            } catch (NumberFormatException e) {
                postView.printMessage("번호는 숫자로 입력해주세요.");
            } catch (IndexOutOfBoundsException | IllegalArgumentException | PostNotFoundException e) {
                postView.printMessage(e.getMessage());
            }
        }
    }

    // 게시글 작성
    public void post() {
        String title = postView.readTitle();
        String content = postView.readContent();
        Category category = postView.readCategory();
        postService.createPost(title, content, category);
        postView.printMessage("게시글이 작성되었다!!");
    }

    // 게시글 목록 조회
    public void getPosts() {
        postView.getPostsView(postService.getPosts());
    }
    
    // 게시글 단건 조회
    public void getPost() {
        long postId = postView.readPostNumber();
        Post post = postService.getPost(postId);
        postView.getPostView(post);
    }

    // 게시글 수정 메서드. 제목과 본문을 수정할 수 있다.
    public void updatePost() {
        long postId = postView.readPostNumber();
        // 수정할 값을 묻기 전에 게시글 존재 여부 먼저 확인
        postService.getPost(postId);

        String title = postView.readTitle();
        String content = postView.readContent();
        postService.updatePost(postId, title, content);
        postView.printMessage("게시물 수정완료~~~!");
    }

    //게시글 삭제
    public void deletePost() {
        long postId = postView.readPostNumber();
        postService.deletePost(postId);
        postView.printMessage("게시물 영구적으로 삭제됨. 복구못해요");
    }
//
//    // Out of index 방지를 위한 인덱스 체크 및 입력 호출 메서드
//    public int checkAndReadIndex() {
//        if (posts.isEmpty()) {
//            throw new IndexOutOfBoundsException("작성된 게시물이 없습니다.");
//        }
//
//        int index = postView.readPostNumber();
//        if (index < 0 || index >= posts.size()) {
//            throw new IndexOutOfBoundsException("존재하는 게시물이 아니에요!!!! 아웃오브인덱스!!");
//        }
//        return index;
//    }
}

