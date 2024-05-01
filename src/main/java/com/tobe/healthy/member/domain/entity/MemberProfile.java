package com.tobe.healthy.member.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

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

	private String fileName;

	private String originalName;

	@OneToOne(mappedBy = "memberProfile", fetch = LAZY)
	private Member member;

	private Long fileSize;

	@Nullable
	private String fileUrl;

	public static MemberProfile create(String originalName, Long fileSize, String fileUrl, Member member) {
		return MemberProfile.builder()
			.originalName(originalName)
			.fileSize(fileSize)
			.fileUrl(fileUrl)
			.member(member)
			.build();
	}

	public static MemberProfile create(String originalName, Long fileSize, String fileUrl) {
		return MemberProfile.builder()
			.originalName(originalName)
			.fileSize(fileSize)
			.fileUrl(fileUrl)
			.build();
	}
}
