package org.sopt.post.presentation;

import org.sopt.post.domain.Category;
import org.sopt.post.domain.Post;

import java.util.ArrayList;
import java.util.List;


// 메뉴에 따라 입력받고 서비스를 호출한 뒤 결과·예외를 안내해주는 역할
public class PostController {
    List<Post> posts = new ArrayList<>();
    PostView postView = new PostView();

    public void run() {
        while (true) {
            postView.printMenu();
            int command = postView.setCommand();// command 입력받아서 저장

            switch (command) {
                case 1:
                    this.post();
                    break;

                case 2:
                    this.getPosts();
                    break;

                case 3:
                    this.getPost();
                    break;

                case 4:
                    this.updatePost();
                    break;

                case 5:
                    this.deletePost();
                    break;

                case 6:
                    System.out.println("프로그램을 종료합니다.");
                    return;
            }
        }
    }

    // 게시글 작성
    public void post() {
        String t = postView.readTitle();
        String c = postView.readContent();

        try{
            Category category = postView.readCategory();
            posts.add(new Post(t, c, category));
            System.out.println("게시글이 작성되었습니다.");

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

    }

    // 게시글 목록 조회
    public void getPosts() {
        postView.getPostsView(posts);
    }
    
    // 게시글 단건 조회
    public void getPost() {
        try {
            int readIndex = checkAndReadIndex();
            Post readPost = posts.get(readIndex);
            postView.getPostView(readPost);

        } catch (IndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        }
    }

    // 게시글 수정 메서드. 제목과 본문을 수정할 수 있다.
    public void updatePost() {
        try {
            int readIndex = checkAndReadIndex();
            Post updatePost = posts.get(readIndex);

            String s = postView.readTitle();
            String c = postView.readContent();

            updatePost.update(s, c);
            postView.printMessage("게시글이 수정되었습니다.");

        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

    }

    //게시글 삭제
    public void deletePost() {
        try {
            int readIndex = checkAndReadIndex();
            posts.remove(readIndex);
            postView.printMessage("게시글이 삭제되었습니다.");

        } catch (IndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        }

    }

    // Out of index 방지를 위한 인덱스 체크 및 입력 호출 메서드
    public int checkAndReadIndex() {
        if (posts.isEmpty()) {
            throw new IndexOutOfBoundsException("작성된 게시물이 없습니다.");
        }

        int index = postView.readPostNumber();
        if (index < 0 || index >= posts.size()) {
            throw new IndexOutOfBoundsException("존재하는 게시물이 아니에요!!!! 아웃오브인덱스!!");
        }
        return index;
    }
}

