package org.sopt;

import java.util.List;
import java.util.Scanner;

public class PostView {

    Scanner scanner = new Scanner(System.in);

    public void printMenu() {
        System.out.println("\n=== 게시판 ===");
        System.out.println("1. 게시글 작성");
        System.out.println("2. 게시글 목록 조회");
        System.out.println("3. 게시글 단건 조회");
        System.out.println("4. 게시글 수정");
        System.out.println("5. 게시글 삭제");
        System.out.println("6. 종료");
        System.out.print("선택: ");
    }

    public int setCommand() {
        return Integer.parseInt(scanner.nextLine());
    }

    public String readTitle() {
        System.out.print("제목: ");
        return scanner.nextLine();
    }

    public String readContent() {
        System.out.print("내용: ");
        return scanner.nextLine();
    }

    public void getPostsView(List<Post> posts) {

        System.out.println("\n=== 게시글 목록 ===");

        if (posts.isEmpty()) {
            System.out.println("게시글이 없습니다.");
        }

        for (int i = 0; i < posts.size(); i++) {
            Post currentPost = posts.get(i);

            System.out.println(
                    (i + 1) + ". " + currentPost.getTitle()
            );
        }
    }


    public void getAfterPostView(Post readPost) {
        System.out.println("\n=== 게시글 ===");
        System.out.println("제목: " + readPost.getTitle());
        System.out.println("내용: " + readPost.getContent());
    }

    public void printMessage(String s) {
        System.out.println(s);
    }

    public int readPostNumber() {
        System.out.println("수정/삭제할 게시글 번호: ");
        return Integer.parseInt(scanner.nextLine()) - 1;
    }
}
