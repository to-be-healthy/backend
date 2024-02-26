package com.tobe.healthy.member.domain.entity;

import static com.tobe.healthy.member.domain.entity.Alarm.ABLE;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.file.domain.entity.Files;
import com.tobe.healthy.member.domain.dto.in.MemberRegisterCommand;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public static Member create(MemberRegisterCommand request, String password) {
        Member member = new Member();
        member.email = request.getEmail();
        member.password = password;
        member.nickname = request.getNickname();
        member.isAlarm = ABLE;
        member.category = request.getCategory();
        return member;
    }
}
