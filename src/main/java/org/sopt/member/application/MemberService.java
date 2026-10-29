package org.sopt.member.application;

import org.sopt.member.application.exception.MemberNotFoundException;
import org.sopt.member.domain.Member;
import org.sopt.member.domain.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public long createMember(String name, String memberPhoneNumber) {
        Member member = new Member(name, memberPhoneNumber);
        Member savedMember = memberRepository.save(member);

        return savedMember.getMemberId();
    }

    @Transactional
    public void deleteMember(long id) {
        memberRepository.findById(id).orElseThrow(() ->
                        new MemberNotFoundException("회원을 찾을 수 없습니다."));

        memberRepository.deleteById(id);
    }

}
