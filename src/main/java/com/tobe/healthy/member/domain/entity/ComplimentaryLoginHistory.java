package com.tobe.healthy.member.domain.entity;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import com.tobe.healthy.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "complimentary_login_history")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class ComplimentaryLoginHistory extends BaseTimeEntity<ComplimentaryLoginHistory, Long> {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "complimentary_login_history_id")
	private Long id;

	@ManyToOne(fetch = LAZY, optional = false)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Column(nullable = false)
	private String userId;

	@Enumerated(STRING)
	@Column(nullable = false, length = 20)
	private MemberType memberType;

	public static ComplimentaryLoginHistory create(Member member) {
		return ComplimentaryLoginHistory.builder()
			.member(member)
			.userId(member.getUserId())
			.memberType(member.getMemberType())
			.build();
	}
}
