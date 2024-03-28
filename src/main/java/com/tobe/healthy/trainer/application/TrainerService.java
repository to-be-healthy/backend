package com.tobe.healthy.trainer.application;

import com.tobe.healthy.common.RedisKeyPrefix;
import com.tobe.healthy.common.RedisService;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.config.error.ErrorCode;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberType;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.trainer.domain.dto.TrainerMemberMappingDto;
import com.tobe.healthy.trainer.domain.dto.in.MemberInviteCommand;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.tobe.healthy.config.error.ErrorCode.MAIL_SEND_ERROR;


@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerService {

    private final JavaMailSender mailSender;
    private final RedisService redisService;
    private final MemberRepository memberRepository;
    private final TrainerMemberMappingRepository mappingRepository;

    public TrainerMemberMappingDto addMemberOfTrainer(Long trainerId, Long memberId) {
        memberRepository.findByIdAndMemberTypeAndDelYnFalse(trainerId, MemberType.TRAINER)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        mappingRepository.findByTrainerIdAndMemberId(trainerId, memberId)
                .ifPresent(i -> {throw new CustomException(ErrorCode.MEMBER_ALREADY_MAPPED);});
        TrainerMemberMapping mapping = TrainerMemberMapping.create(trainerId, memberId);
        mappingRepository.save(mapping);
        return TrainerMemberMappingDto.from(mapping);
    }

    public void inviteMember(MemberInviteCommand command, Member trainer) {
        memberRepository.findByIdAndMemberTypeAndDelYnFalse(trainer.getId(), MemberType.TRAINER)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        List<String> emails = command.getEmails();
        for(String email : emails){
            memberRepository.findByEmail(email).ifPresent(i -> {throw new CustomException(ErrorCode.MEMBER_ALREADY_MAPPED);});
            String uuid = System.currentTimeMillis() + "_" + UUID.randomUUID();
            String invitationKey = RedisKeyPrefix.INVITATION.getDescription() + uuid;
            String invitationLink = "https://www.to-be-healthy.site?" + uuid;
            sendInviteLink(email, trainer, invitationLink);
            Map<String, String> invitedMapping = new HashMap<>() {{
                put("trainerId", trainer.getId().toString());
                put("email", email);
            }};
            redisService.setValuesWithTimeout(invitationKey, JSONObject.toJSONString(invitedMapping), 24 * 60 * 60 * 1000); // 1days
        }
    }

    private void sendInviteLink(String email, Member trainer, String invitationLink) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("[건강해짐] 안녕하세요. {trainerName}님이 고객님을 초대했습니다.".replace("{trainerName}", trainer.getName())); // 메일 제목
            String text = "안녕하세요. {trainerName}님이 고객님을 초대했습니다.\n하단 링크를 통해 회원가입을 해주세요.\n{inviteLink}"
                    .replace("{trainerName}", trainer.getName())
                    .replace("{inviteLink}", invitationLink);
            mimeMessageHelper.setText(text, false); // 메일 본문 내용, HTML 여부
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(MAIL_SEND_ERROR);
        }
    }
}
