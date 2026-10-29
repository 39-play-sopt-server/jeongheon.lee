package org.sopt.member.domain;

import jakarta.persistence.*;
import org.sopt.common.domain.BaseEntity;

@Entity
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private long memberId;

    @Column(name = "member_name", nullable = false)
    private String memberName;

    @Column(name = "member_phone_number", nullable = true)
    private String memberPhoneNumber;
    
    // 유저는 회원 생성을 할 수 있다 -> 회원가입 할 수 있다
    
    // 회원은 회원 삭제를 할 수 있다 -> 회원탈퇴 할 수 있다
}
