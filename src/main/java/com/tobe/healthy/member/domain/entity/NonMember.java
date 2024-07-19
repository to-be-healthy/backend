package com.tobe.healthy.member.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;


@Entity
@Builder
@ToString
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class NonMember {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "nonmember_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    private Member member;

    private String invitationLink;
    private String name;
    private Long trainerId;
    private int lessonCnt;

    public static NonMember create(Member member, String invitationLink, String name, Long trainerId, int lessonCnt) {
        return NonMember.builder()
                .member(member)
                .invitationLink(invitationLink)
                .name(name)
                .trainerId(trainerId)
                .lessonCnt(lessonCnt)
                .build();
    }
}
