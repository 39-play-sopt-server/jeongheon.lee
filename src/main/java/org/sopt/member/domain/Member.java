package org.sopt.member.domain;

import jakarta.persistence.*;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private long memberId;

    @Column(name = "member_name", nullable = false)
    private String memberName;

    @Column(name = "member_phone_number", nullable = true)
    private String memberPhoneNumber;
}
