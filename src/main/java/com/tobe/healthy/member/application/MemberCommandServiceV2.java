package com.tobe.healthy.member.application;

import static com.tobe.healthy.common.error.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.tobe.healthy.common.error.CustomException;
import com.tobe.healthy.file.application.LocalFileStorageService;
import com.tobe.healthy.member.presentation.dto.in.CommandRegisterMemberProfile;
import com.tobe.healthy.member.presentation.dto.out.RegisterMemberProfileResult;
import com.tobe.healthy.member.domain.Member;
import com.tobe.healthy.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberCommandServiceV2 {

	private final MemberRepository memberRepository;
	private final LocalFileStorageService fileStorageService;

	public RegisterMemberProfileResult registerProfile(CommandRegisterMemberProfile request, Long memberId) {
		Member findMember = memberRepository.findMemberById(memberId)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

		if (ObjectUtils.isEmpty(request)) {
			throw new IllegalArgumentException("프로필 사진을 등록해 주세요.");
		}

		String tempFilePath = fileStorageService.extractFilePath(request.getUploadFile().getFileUrl());
		String fileName = tempFilePath.replaceFirst("temp/", "");
		String originPath = "origin/profile/" + fileName;

		fileStorageService.copy(tempFilePath, originPath);

		String fileUrl = fileStorageService.getFileUrl(originPath);

		log.info("등록한 fileUrl: {}", fileUrl);

		findMember.registerProfile(fileName, fileUrl);

		return RegisterMemberProfileResult.from(fileUrl, fileName);
	}
}
