package org.sopt.post.presentation;

import org.sopt.post.domain.Category;
import org.sopt.post.domain.Comment;
import org.sopt.post.domain.Post;

import java.util.List;
import java.util.Scanner;

// 콘솔 입력과 출력 담당
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
                    currentPost.getPostId() + ". " + currentPost.getTitle()
            );
        }
    }


    public void getPostView(Post readPost) {
        System.out.println("\n=== 게시글 ===");
        System.out.println("카테고리: " + readPost.getCategory());
        System.out.println("제목: " + readPost.getTitle());
        System.out.println("내용: " + readPost.getContent());
        System.out.println("좋아요: " + readPost.getLikes());
        
        // 댓글이 있으면  출력, 없으면 스킵
        if (!readPost.getComments().isEmpty()){
            System.out.println("댓글: ");
                for (Comment comment : readPost.getComments()) {
                    System.out.println(comment.getAuthor() + ": " + comment.getContent()
                    + "좋아요: " + comment.getLikes());
            }
        }
    }

    public void printMessage(String s) {
        System.out.println(s);
    }

    public long readPostNumber() {
        System.out.println("게시글 번호를 입력하세요: ");
        return Long.parseLong(scanner.nextLine());
    }

    // 게시물 카테고리를 선택할 수 있게 한다.
    // 입력을 숫자로 했는지, 목록에 있는 번호를 선택했는지 검증한다.
    public Category readCategory() {
        Category[] categories = Category.values();

        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i]);
        }

        System.out.print("카테고리 번호: ");

        int number;
        try {
            number = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("카테고리 번호는 숫자로 입력해주세요.");
        }

        if (number < 1 || number > categories.length) {
            throw new IllegalArgumentException("목록에 있는 카테고리 번호를 선택해주세요.");
        }

        return categories[number - 1];
    }
}
