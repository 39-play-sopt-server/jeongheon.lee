package org.sopt.post.application;

import org.junit.jupiter.api.Test;
import org.sopt.member.application.exception.WithdrawnMemberException;
import org.sopt.member.domain.Member;
import org.sopt.member.domain.MemberRepository;
import org.sopt.post.application.dto.PostResult;
import org.sopt.post.domain.Category;
import org.sopt.post.domain.Post;
import org.sopt.post.domain.PostRepository;
import org.sopt.post.presentation.dto.PostResponse;
import org.sopt.post.presentation.dto.PostPageResponse;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    private final PostRepository postRepository = mock(PostRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final PostService postService = new PostService(postRepository, memberRepository);

    @Test
    void createPostReturnsAuthor() {
        Member member = member(1L, "작성자");
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            ReflectionTestUtils.setField(post, "postId", 10L);
            return post;
        });

        PostResult result = postService.createPost(1L, "제목", "내용", Category.IT);

        assertEquals(10L, result.id());
        assertEquals("작성자", PostResponse.from(result).authorName());
        assertFalse(result.authorWithdrawn());
    }

    @Test
    void withdrawnMemberCannotCreatePost() {
        Member member = member(1L, "작성자");
        member.withdraw();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        assertThrows(WithdrawnMemberException.class,
                () -> postService.createPost(1L, "제목", "내용", Category.IT));

        verifyNoInteractions(postRepository);
    }

    @Test
    void withdrawnMembersPostRemainsReadable() {
        Member member = member(1L, "작성자");
        member.withdraw();
        Post post = post(10L, 1L);
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        PostResult result = postService.getPostDetail(10L);

        assertEquals(10L, result.id());
        assertEquals(1L, result.viewCount());
        assertTrue(result.authorWithdrawn());
        assertEquals("탈퇴한 회원", PostResponse.from(result).authorName());
    }

    @Test
    void getPostsMatchesAuthorsByIdWithOneMemberQuery() {
        Member first = member(1L, "첫 작성자");
        Member second = member(2L, "둘째 작성자");
        second.withdraw();
        when(postRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(
                List.of(post(10L, 1L), post(20L, 2L), post(30L, 1L)),
                PageRequest.of(0, 3), 7));
        when(memberRepository.findAllByMemberIdIn(List.of(1L, 2L)))
                .thenReturn(List.of(second, first));

        Page<PostResult> results = postService.getPosts(0, 3);
        PostPageResponse response = PostPageResponse.from(results);
        List<PostResponse> responses = response.content();

        assertEquals(List.of(10L, 20L, 30L), responses.stream().map(PostResponse::id).toList());
        assertEquals(List.of("첫 작성자", "탈퇴한 회원", "첫 작성자"),
                responses.stream().map(PostResponse::authorName).toList());
        verify(memberRepository).findAllByMemberIdIn(List.of(1L, 2L));
        verify(memberRepository, never()).findById(anyLong());

        assertEquals(0, response.page());
        assertEquals(3, response.size());
        assertEquals(7L, response.totalElements());
        assertEquals(3, response.totalPages());
        assertTrue(response.hasNext());

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(postRepository).findAll(pageable.capture());
        assertEquals(0, pageable.getValue().getPageNumber());
        assertEquals(3, pageable.getValue().getPageSize());
        assertEquals(Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("postId")),
                pageable.getValue().getSort());
    }

    @Test
    void emptyPagePreservesTotalsWithoutQueryingMembers() {
        when(postRepository.findAll(any(Pageable.class))).thenReturn(
                new PageImpl<>(List.of(), PageRequest.of(3, 10), 25));

        PostPageResponse response = PostPageResponse.from(postService.getPosts(3, 10));

        assertTrue(response.content().isEmpty());
        assertEquals(3, response.page());
        assertEquals(25L, response.totalElements());
        assertEquals(3, response.totalPages());
        assertFalse(response.hasNext());

        verifyNoInteractions(memberRepository);
    }

    @Test
    void invalidPaginationDoesNotQueryRepositories() {
        assertThrows(IllegalArgumentException.class, () -> postService.getPosts(-1, 10));
        assertThrows(IllegalArgumentException.class, () -> postService.getPosts(0, 0));
        assertThrows(IllegalArgumentException.class, () -> postService.getPosts(0, 101));

        verifyNoInteractions(postRepository, memberRepository);
    }

    private Member member(long id, String name) {
        Member member = new Member(name, "01012345678");
        ReflectionTestUtils.setField(member, "memberId", id);
        return member;
    }

    private Post post(long id, long memberId) {
        Post post = new Post(memberId, "제목", "내용", Category.IT);
        ReflectionTestUtils.setField(post, "postId", id);
        return post;
    }
}
