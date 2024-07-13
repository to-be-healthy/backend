package com.tobe.healthy.member.application;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.tobe.healthy.common.error.CustomException;
import com.tobe.healthy.member.domain.dto.in.CommandRegisterMemberProfile;
import com.tobe.healthy.member.domain.dto.out.RegisterMemberProfileResult;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import static com.tobe.healthy.common.Utils.CDN_DOMAIN;
import static com.tobe.healthy.common.Utils.S3_DOMAIN;
import static com.tobe.healthy.common.error.ErrorCode.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberCommandServiceV2 {

    private final MemberRepository memberRepository;
    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public RegisterMemberProfileResult registerProfile(CommandRegisterMemberProfile request, Long memberId) {
        Member findMember = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (ObjectUtils.isEmpty(request)) {
            throw new IllegalArgumentException("프로필 사진을 등록해 주세요.");
        }

        String tempUrl = request.getUploadFile().getFileUrl().replace(S3_DOMAIN, "");
        String fileName = tempUrl.replaceFirst("temp/", "");

        String createdOriginUrl = "origin/profile/" + fileName;

        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(
                bucketName,
                tempUrl,
                bucketName,
                createdOriginUrl
        );

        amazonS3.copyObject(copyObjectRequest);

        String fileUrl = amazonS3.getUrl(bucketName, createdOriginUrl).toString().replace(S3_DOMAIN, CDN_DOMAIN);

        log.info("등록한 fileUrl: {}", fileUrl);

        findMember.registerProfile(fileName, fileUrl);

        return RegisterMemberProfileResult.from(fileUrl, fileName);
    }
}