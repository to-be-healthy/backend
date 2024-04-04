package com.tobe.healthy.gym.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.gym.domain.dto.in.MembershipAddCommand;
import com.tobe.healthy.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@DynamicUpdate
public class GymMembership extends BaseTimeEntity<GymMembership, Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "gym_membership_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "gym_id")
    private Gym gym;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDate gymStartDt;
    private LocalDate gymEndDt;

    @Builder
    public GymMembership(Gym gym, Member member, LocalDate gymStartDt, LocalDate gymEndDt) {
        this.gym = gym;
        this.member = member;
        this.gymStartDt = gymStartDt;
        this.gymEndDt = gymEndDt;
    }
}