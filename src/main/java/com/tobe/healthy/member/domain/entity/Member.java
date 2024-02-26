package com.tobe.healthy.member.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.file.domain.entity.Files;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Getter
@Builder
public class Member extends BaseTimeEntity<Member, Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String email;

    private String password;

    private String nickname;

    @OneToOne
    @JoinColumn(name = "files_id")
    private Files files;

    @Enumerated(STRING)
    private Alarm isAlarm;

    @Enumerated(STRING)
    private MemberCategory category;

    public static Member create(String email, String password, String nickname) {
        Member member = new Member();
        member.email = email;
        member.password = password;
        member.nickname = nickname;
        return member;
    }
}
