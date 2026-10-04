package org.sopt;

import java.util.ArrayList;
import java.util.List;

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
        posts.add(new Post(t, c));

        System.out.println("게시글이 작성되었습니다.");
    }

    // 게시글 목록 조회
    public void getPosts() {
        postView.getPostsView(posts);
    }
    
    // 게시글 단건 조회
    public void getPost() {

        int readIndex = checkIndex();
        if (readIndex == -1) {
            postView.printMessage("해당하는 게시글이 없습니다.");
            return;
        }
        Post readPost = posts.get(readIndex);
        postView.getAfterPostView(readPost);
    }

    // 게시글 수정
    public void updatePost() {

        int updateIndex = checkIndex();
        if (updateIndex == -1){
            return;
        }

        Post updatePost = posts.get(updateIndex);

        // 업데이트 로직
        updatePost.setTitle(postView.readTitle());
        updatePost.setContent(postView.readContent());

        postView.printMessage("게시글이 수정되었습니다.");
    }

    //게시글 삭제
    public void deletePost() {

        int deleteIndex = checkIndex();
        if (deleteIndex == -1) {
            return;
        }
        posts.remove(deleteIndex);
        postView.printMessage("게시글이 삭제되었습니다.");
    }

    // Out of index 방지를 위한 인덱스 체크 및 입력 호출 메서드
    public int checkIndex() {
        if (posts.isEmpty()) {
            postView.printMessage("게시글이 없습니다.");
            return -1;
        }

        int index = postView.readPostNumber();
        if (index < 0 || index >= posts.size()) {
            postView.printMessage("존재하지 않는 게시글입니다.");
            return -1;
        }
        return index;
    }
}

