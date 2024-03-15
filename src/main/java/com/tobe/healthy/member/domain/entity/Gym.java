package com.tobe.healthy.member.domain.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.tobe.healthy.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@Builder
@AllArgsConstructor
@DynamicUpdate
public class Gym extends BaseTimeEntity<Gym, Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "gym_id")
    private Long id;

    private String name;

    @OneToOne(mappedBy = "gym")
    private Member member;
}
