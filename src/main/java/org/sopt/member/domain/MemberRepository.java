package org.sopt.member.domain;


import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends Repository<Member, Long> {
    Member save(Member member);

    Optional<Member> findById(Long memberId);

    void deleteById(Long memberId);

    List<Member> getMemberByMemberId(long memberId);
}
