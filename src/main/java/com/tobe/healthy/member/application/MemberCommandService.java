package com.tobe.healthy.member.application;

import static com.tobe.healthy.common.Utils.CDN_DOMAIN;
import static com.tobe.healthy.common.Utils.S3_DOMAIN;
import static com.tobe.healthy.common.Utils.createFileName;
import static com.tobe.healthy.common.Utils.createObjectMetadata;
import static com.tobe.healthy.common.error.ErrorCode.FILE_UPLOAD_ERROR;
import static com.tobe.healthy.common.error.ErrorCode.MAIL_AUTH_CODE_NOT_VALID;
import static com.tobe.healthy.common.error.ErrorCode.MEMBER_EMAIL_DUPLICATION;
import static com.tobe.healthy.common.error.ErrorCode.MEMBER_NAME_LENGTH_NOT_VALID;
import static com.tobe.healthy.common.error.ErrorCode.MEMBER_NAME_NOT_VALID;
import static com.tobe.healthy.common.error.ErrorCode.MEMBER_NOT_FOUND;
import static com.tobe.healthy.common.error.ErrorCode.MEMBER_NOT_MAPPED;
import static com.tobe.healthy.common.error.ErrorCode.NOT_MATCH_PASSWORD;
import static com.tobe.healthy.common.error.ErrorCode.PASSWORD_POLICY_VIOLATION;
import static io.micrometer.common.util.StringUtils.isEmpty;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.tobe.healthy.common.Utils;
import com.tobe.healthy.common.error.CustomException;
import com.tobe.healthy.common.redis.RedisService;
import com.tobe.healthy.config.OAuthProperties;
import com.tobe.healthy.member.domain.dto.in.CommandAssignNickname;
import com.tobe.healthy.member.domain.dto.in.CommandChangeEmail;
import com.tobe.healthy.member.domain.dto.in.CommandChangeMemberPassword;
import com.tobe.healthy.member.domain.dto.in.CommandChangeName;
import com.tobe.healthy.member.domain.dto.in.CommandUpdateMemo;
import com.tobe.healthy.member.domain.dto.in.OAuthInfo;
import com.tobe.healthy.member.domain.dto.out.CommandAssignNicknameResult;
import com.tobe.healthy.member.domain.dto.out.CommandChangeNameResult;
import com.tobe.healthy.member.domain.dto.out.DeleteMemberProfileResult;
import com.tobe.healthy.member.domain.dto.out.MemberChangeAlarmResult;
import com.tobe.healthy.member.domain.dto.out.RegisterMemberProfileResult;
import com.tobe.healthy.member.domain.entity.AlarmStatus;
import com.tobe.healthy.member.domain.entity.AlarmType;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.point.repository.PointRepository;
import com.tobe.healthy.push.repository.MemberTokenRepository;
import com.tobe.healthy.trainer.application.TrainerService;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberCommandService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RedisService redisService;
    private final TrainerMemberMappingRepository mappingRepository;
    private final TrainerService trainerService;
    private final PointRepository pointRepository;
    private final AmazonS3 amazonS3;
    private final MemberTokenRepository memberTokenRepository;
    private final WebClient webClient;
    private final OAuthProperties oAuthProperties;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public void logout(Long memberId) {
        memberRepository.findById(memberId).ifPresent(m -> {
            // 1. refresh token 삭제
            redisService.deleteValues(m.getUserId());
            // 2. fcm token 삭제
            memberTokenRepository.deleteAll(m.getMemberToken());
        });
	}

    public String deleteMember(Member loginMember) {
        Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        switch (member.getSocialType()) {
            case KAKAO -> {
                webClient.post()
                    .uri("https://kapi.kakao.com/v1/user/unlink")
                    .header("Authorization", "KakaoAK 4619cf37473b70ea6a53c33c1c14ec23")
                    .body(BodyInserters.fromFormData("target_id_type", "user_id").with("target_id", String.valueOf(member.getSocialId())))
                    .retrieve().bodyToMono(String.class).share().block();
            }
            case NAVER -> {
                MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
                request.add("client_id", oAuthProperties.getNaver().getClientId());
                request.add("client_secret", oAuthProperties.getNaver().getClientSecret());
                request.add("refresh_token", member.getSocialRefreshToken());
                request.add("grant_type", oAuthProperties.getNaver().getGrantType());

                OAuthInfo token = webClient.post()
                    .uri(oAuthProperties.getNaver().getTokenUri())
                    .bodyValue(request)
                    .headers(header -> header.setContentType(APPLICATION_FORM_URLENCODED))
                    .retrieve()
                    .bodyToMono(OAuthInfo.class)
                    .share()
                    .block();

                MultiValueMap<String, String> deleteToken = new LinkedMultiValueMap<>();
                deleteToken.add("client_id", oAuthProperties.getNaver().getClientId());
                deleteToken.add("client_secret", oAuthProperties.getNaver().getClientSecret());
                deleteToken.add("access_token", token.getAccessToken());
                deleteToken.add("grant_type", "delete");

                webClient.post().
                    uri(oAuthProperties.getNaver().getTokenUri())
                    .bodyValue(deleteToken)
                    .headers(header -> header.setContentType(APPLICATION_FORM_URLENCODED))
                    .retrieve()
                    .bodyToMono(String.class)
                    .share().block();
            }
            case APPLE -> {
                MultiValueMap<String, String> revokeForm = new LinkedMultiValueMap<>();
                revokeForm.add("client_id", "tobehealthy.apple.login");
                revokeForm.add("client_secret", member.getSocialId());
                revokeForm.add("token", member.getSocialRefreshToken());
                revokeForm.add("token_type_hint", "refresh_token");

                webClient.post().
                    uri("https://appleid.apple.com/auth/revoke")
                    .bodyValue(revokeForm)
                    .headers(header -> header.setContentType(APPLICATION_FORM_URLENCODED))
                    .retrieve()
                    .bodyToMono(String.class)
                    .share().block();
            }
        }
        switch (member.getMemberType()){
            case TRAINER: //트레이너 탈퇴시 학생들 환불처리 & 매핑끊기
                Long trainerId = member.getId();
                List<TrainerMemberMapping> mappings = mappingRepository.findAllByTrainerId(trainerId);
                if(!mappings.isEmpty()){
                    for(TrainerMemberMapping mapping : mappings){
                        trainerService.refundStudentOfTrainer(mapping.getTrainer(), mapping.getMember().getId());
                        pointRepository.deleteByMember(mapping.getMember());
                        log.info("[트레이너 탈퇴] trainer: {}, deleteMapping: {}, refundMember: {}", member, mapping, mapping.getMember());
                    }
                }
                break;

            case STUDENT: //학생 탈퇴시 환불처리 & 매핑끊기
                Long memberId = member.getId();
                Optional<TrainerMemberMapping> mappingOpt = mappingRepository.findByMemberId(memberId);
                if(mappingOpt.isPresent()){
                    TrainerMemberMapping mapping = mappingOpt.get();
                    trainerService.refundStudentOfTrainer(mapping.getTrainer(), mapping.getMember().getId());
                    pointRepository.deleteByMember(mapping.getMember());
                }
                log.info("[학생 탈퇴] member: {}, deleteMappings: {}, trainer: {}",
                        member,
                        mappingOpt.orElse(null),
                        mappingOpt.<Object>map(TrainerMemberMapping::getTrainer).orElse(null));
                break;
        }
        member.deleteMember();
        return member.getUserId();
    }

    public boolean changePassword(CommandChangeMemberPassword request, Long memberId) {
        if (!request.getChangePassword1().equals(request.getChangePassword2())) {
            throw new CustomException(NOT_MATCH_PASSWORD);
        }

        if (Utils.validatePassword(request.getChangePassword1())) {
            throw new CustomException(PASSWORD_POLICY_VIOLATION);
        }

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (passwordEncoder.matches(request.getChangePassword1(), member.getPassword())) {
            throw new IllegalArgumentException("이전 비밀번호와 동일합니다.");
        }

        String password = passwordEncoder.encode(request.getChangePassword1());

        member.changePassword(password);

        return true;
    }

    public RegisterMemberProfileResult registerProfile(MultipartFile uploadFile, Long memberId) {
        Member findMember = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (uploadFile.isEmpty()) {
            throw new IllegalArgumentException("프로필 사진을 등록해 주세요.");
        }

        ObjectMetadata objectMetadata = createObjectMetadata(uploadFile.getSize(), uploadFile.getContentType());
        String savedFileName = createFileName("origin/profile/") + uploadFile.getOriginalFilename().substring(uploadFile.getOriginalFilename().lastIndexOf("."));;

        try (InputStream inputStream = uploadFile.getInputStream()) {
            amazonS3.putObject(
                bucketName,
                savedFileName,
                inputStream,
                objectMetadata
            );

            String fileUrl = amazonS3.getUrl(bucketName, savedFileName).toString().replaceAll(S3_DOMAIN, CDN_DOMAIN);

            findMember.registerProfile(savedFileName, fileUrl);

            return RegisterMemberProfileResult.from(fileUrl, savedFileName);
        } catch (IOException e) {
            log.error("error => {}", e.getStackTrace()[0]);
            throw new CustomException(FILE_UPLOAD_ERROR);
        }
    }

    public DeleteMemberProfileResult deleteProfile(Long memberId) {
        Member findMember = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (ObjectUtils.isEmpty(findMember.getMemberProfile())) {
            throw new IllegalArgumentException("프로필 사진이 없습니다.");
        }

        String fileUrl = findMember.getMemberProfile().getFileUrl();
        String fileName = findMember.getMemberProfile().getFileName();

        amazonS3.deleteObject(bucketName, fileName);

        findMember.deleteProfile();

        return DeleteMemberProfileResult.from(fileUrl, fileName);
    }

    public CommandChangeNameResult changeName(CommandChangeName request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        validateName(request.getName());
        member.changeName(request.getName());
        return CommandChangeNameResult.from(member);
    }

    public MemberChangeAlarmResult changeAlarm(AlarmType alarmType, AlarmStatus alarmStatus, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        member.changeAlarm(alarmType, alarmStatus);
        return MemberChangeAlarmResult.from(alarmType, alarmStatus);
    }

    public void updateMemo(Long trainerId, Long mmeberId, CommandUpdateMemo command) {
        memberRepository.findById(mmeberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        TrainerMemberMapping mapping = mappingRepository.findByTrainerIdAndMemberId(trainerId, mmeberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_MAPPED));
        mapping.changeMemo(command.getMemo());
    }

    public CommandAssignNicknameResult assignNickname(CommandAssignNickname request, Long studentId) {
        Member member = memberRepository.findById(studentId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        member.assignNickname(request.getNickname());
        return CommandAssignNicknameResult.from(member);
    }

    public Boolean changeEmail(CommandChangeEmail request, Long memberId) {
        memberRepository.findByEmail(request.getEmail()).ifPresent(m -> {
            throw new CustomException(MEMBER_EMAIL_DUPLICATION);
        });

        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        String value = redisService.getValues(request.getEmail());

        if (isEmpty(value) || !value.equals(request.getEmailKey())) {
            throw new CustomException(MAIL_AUTH_CODE_NOT_VALID);
        }

        findMember.changeEmail(request.getEmail());
        redisService.deleteValues(request.getEmail());

        return true;
    }

    public Boolean changeScheduleNotice(AlarmStatus alarmStatus, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        member.changeScheduleNotice(alarmStatus);
        return true;
    }

    public Boolean changeTrainerFeedback(AlarmStatus alarmStatus, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        member.changeTrainerFeedback(alarmStatus);
        return true;
    }

    private void validateName(String name) {
        if (Utils.validateNameLength(name)) {
            throw new CustomException(MEMBER_NAME_LENGTH_NOT_VALID);
        }

        if (Utils.validateNameFormat(name)) {
            throw new CustomException(MEMBER_NAME_NOT_VALID);
        }
    }

    public Boolean changeDietNotice(AlarmStatus alarmStatus, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        member.changeDietNotice(alarmStatus);
        return true;
    }
}
