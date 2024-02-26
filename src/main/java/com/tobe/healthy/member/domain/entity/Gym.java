package com.tobe.healthy.member.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
public class Gym extends BaseTimeEntity<Gym, Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "gym_id")
    private Long id;

    private String name;
}
