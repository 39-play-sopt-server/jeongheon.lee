package org.sopt.post.application;

import org.sopt.member.application.exception.WithdrawnMemberException;
import org.sopt.member.domain.Member;
import org.sopt.member.domain.MemberRepository;
import org.sopt.member.application.exception.MemberNotFoundException;
import org.sopt.post.application.dto.PostResult;
import org.sopt.post.application.exception.PostNotFoundException;
import org.sopt.post.domain.Category;
import org.sopt.post.domain.Post;
import org.sopt.post.domain.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 게시글 작성·조회·수정·삭제의 순서 조율 담당
@Service
public class PostService {
    private final PostRepository repository;
    private final MemberRepository memberRepository;
    private final Object postLock = new Object(); // 하나의 공유 락

    // 생성자
    public PostService(PostRepository repository, MemberRepository memberRepository) {
        this.repository = repository;
        this.memberRepository = memberRepository;
    }

    // Post를 생성하고 저장소에 보관 후 반환함
    // 빈 제목·본문 검사(도메인 규칙)는 Post가 수행함
    public PostResult createPost(
            Long memberId,
            String title,
            String content,
            Category category
    ) {
//        synchronized는 같은 락을 사용하는 코드 구간을 한 번에 하나의 스레드만 실행하게 하는 Java 문법
        synchronized (postLock) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException("회원을 찾을 수 없습니다!"));

            if (member.isWithdrawn()) {
                throw new WithdrawnMemberException("탈퇴한 회원은 게시글을 작성할 수 없습니다.");
            }

            Post post = new Post(memberId, title, content, category);
            Post savedPost = repository.save(post);
            return PostResult.from(savedPost, member);
        }
    }

    // 게시물 목록 조회
    public List<PostResult> getPosts() {
        synchronized (postLock) {
            List<Post> posts = repository.findAll();
            if (posts.isEmpty()) {
                return List.of();
            }

            // 같은 작성자의 게시글이 여러 개 있어도 회원은 한 번에 조회
            List<Long> memberIds = posts.stream()
                    .map(Post::getMemberId)
                    .distinct()
                    .toList();

            Map<Long, Member> authorById = memberRepository.findAllByMemberIdIn(memberIds)
                    .stream()
                    .collect(Collectors.toMap(Member::getMemberId, member -> member));

            return posts.stream()
                    .map(post -> {
                        Member author = authorById.get(post.getMemberId());
                        if (author == null) {
                            throw new MemberNotFoundException("게시글 작성자를 찾을 수 없습니다.");
                        }
                        return PostResult.from(post, author);
                    })
                    .toList();
        }
    }

    // 게시물 조회해서 DTO 반환(+해당 번호의 게시글이 있는지 확인)
    public PostResult getPostDetail(long postId) {
        synchronized (postLock) {
            Post post = findPostOrThrow(postId);

            Member author = memberRepository.findById(post.getMemberId())
                    .orElseThrow(() -> new MemberNotFoundException("작성자를 찾을 수 없습니다."));

            post.increaseViewCount();
            repository.save(post);

            return PostResult.from(post, author);
        }
    }

    // 게시글을 찾고 수정 규칙의 실행을 Post(애그리거트)에 맡김
    public void updatePost(long postId, String title, String content) {
        synchronized (postLock) {
            // 게시글 존재 여부 먼저 확인
            Post post = findPostOrThrow(postId);

            post.update(title, content);
            repository.save(post);
        }

    }

    // 게시글을 찾고 저장소에 제거를 요청
    public void deletePost(long postId) {
        synchronized (postLock) {
            Post post = findPostOrThrow(postId);
            repository.delete(post);
        }
    }

    // postLock을 획득한 유스케이스 안에서만 호출, 게시물을 못찾으면 예외처리
    private Post findPostOrThrow(long postId) {
        return repository.findById(postId)
                .orElseThrow(() ->
                        new PostNotFoundException("게시글을 찾을 수 없습니다.")
                );
    }
}
