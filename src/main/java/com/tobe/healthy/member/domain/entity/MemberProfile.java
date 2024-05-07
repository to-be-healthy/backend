package com.tobe.healthy.member.domain.entity;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.tobe.healthy.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class MemberProfile extends BaseTimeEntity<MemberProfile, Long> {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "member_profile_id")
	private Long id;

	@OneToOne(mappedBy = "memberProfile", fetch = LAZY, cascade = ALL)
	private Member member;

	@Nullable
	private String fileUrl;

	public static MemberProfile create(String fileUrl, Member member) {
		return MemberProfile.builder()
			.fileUrl(fileUrl)
			.member(member)
			.build();
	}

	public static MemberProfile create(String fileUrl) {
		return MemberProfile.builder()
			.fileUrl(fileUrl)
			.build();
	}
}
