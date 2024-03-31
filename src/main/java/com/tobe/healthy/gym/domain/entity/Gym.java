package com.tobe.healthy.gym.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@DynamicUpdate
public class Gym extends BaseTimeEntity<Gym, Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "gym_id")
    private Long id;

    private String name;

    private int joinCode;

    @OneToMany(fetch = LAZY, mappedBy = "gym")
    private List<Member> member = new ArrayList<>();

	@Builder
	public Gym(String name, List<Member> member, int joinCode) {
		this.name = name;
		this.member = member;
        this.joinCode = joinCode;
	}

    public static Gym registerGym(String name, int accessKey) {
        return Gym.builder()
                .name(name)
                .joinCode(accessKey)
                .build();
    }
}