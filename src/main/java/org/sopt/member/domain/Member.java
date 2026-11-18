package org.sopt.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.sopt.common.domain.BaseEntity;

@Entity
@Getter
public class Member extends BaseEntity {

    private static final int MAX_NAME_LENGTH = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "member_name", nullable = false, length = MAX_NAME_LENGTH)
    private String memberName;

    @Column(name = "member_phone_number", nullable = false)
    private String memberPhoneNumber;

    // 논리 삭제 방식을 위한 컬럼
    @Column(nullable = false)
    private boolean withdrawn = false;

    private Member(String memberName, String memberPhoneNumber, String email, String password) {
        validateMemberName(memberName);
        validateMemberPhoneNumber(memberPhoneNumber);

        this.memberName = memberName;
        this.memberPhoneNumber = memberPhoneNumber;
        this.email = email; // TODO: 이메일, 비번 검증 로직 추가 필요
        this.password = password;
    }

    // 정적 팩토리 메서드
    public static Member signUp(
            String memberName,
            String memberPhoneNumber,
            String email,
            String passwordHash
    ) {
        return new Member(
                memberName,
                memberPhoneNumber,
                email,
                passwordHash
        );
    }

    private void validateMemberName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("회원 이름은 필수입니다.");
        }

        if (name.codePointCount(0, name.length()) > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "회원 이름은 20자 이하여야 합니다."
            );
        }
    }

    private void validateMemberPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("전화번호는 필수입니다.");
        }

        if (!phoneNumber.matches("010[0-9]{8}")) {
            throw new IllegalArgumentException(
                    "전화번호는 하이픈 없이 010으로 시작하는 11자리여야 합니다."
            );
        }
    }

    protected Member() {}

    public void withdraw() {
        this.withdrawn = true;
    }

    // 유저는 회원 생성을 할 수 있다 -> 회원가입 할 수 있다
    
    // 회원은 회원 삭제를 할 수 있다 -> 회원탈퇴 할 수 있다
}
