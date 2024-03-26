package com.tobe.healthy.gym.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
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
@Builder
@AllArgsConstructor
@DynamicUpdate
public class Gym extends BaseTimeEntity<Gym, Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "gym_id")
    private Long id;

    private String name;

    @OneToMany(fetch = LAZY, mappedBy = "gym")
    @Default
    private List<Member> member = new ArrayList<>();

	@Builder
	public Gym(String name) {
		this.name = name;
	}
}
