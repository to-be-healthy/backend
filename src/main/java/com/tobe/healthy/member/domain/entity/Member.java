package com.tobe.healthy.member.domain.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;


import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.file.domain.entity.Files;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
public class Member extends BaseTimeEntity<Member, Long> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long id;
    private String email;
    private String password;
    private String profileId;
    private String nickname;

    @OneToOne
    @JoinColumn(name = "files_id")
    private Files files;

    public static Member create(String email, String password, String nickname) {
        Member member = new Member();
        member.email = email;
        member.password = password;
        member.nickname = nickname;
        return member;
    }
}
