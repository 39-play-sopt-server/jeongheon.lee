package org.sopt.member.application;

import lombok.RequiredArgsConstructor;
import org.sopt.common.JwtProvider;
import org.sopt.member.application.exception.MemberNotFoundException;
import org.sopt.member.domain.Member;
import org.sopt.member.domain.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; // TODO: SequrityConfig에서 Bean 등록 필요

    @Transactional
    public Long signUp(String name, String memberPhoneNumber, String memberEmail, String password) {
        if (memberRepository.existsByEmail(memberEmail)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        String encodedPassword = passwordEncoder.encode(password);
        Member member = Member.signUp(name, memberPhoneNumber, memberEmail, encodedPassword);
        Member savedMember = memberRepository.save(member);

        return savedMember.getMemberId();
    }

    @Transactional
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() ->
                new MemberNotFoundException("회원을 찾을 수 없습니다."));

        member.withdraw();
    }
}
