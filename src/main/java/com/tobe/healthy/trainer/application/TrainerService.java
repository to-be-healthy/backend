package com.tobe.healthy.trainer.application;

import com.tobe.healthy.common.RedisService;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.config.error.ErrorCode;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberType;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.trainer.domain.dto.TrainerMemberMappingDto;
import com.tobe.healthy.trainer.domain.dto.out.MemberInviteCommandResult;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

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

    public MemberInviteCommandResult inviteMember(String email, Member trainer) {
        memberRepository.findByIdAndMemberTypeAndDelYnFalse(trainer.getId(), MemberType.TRAINER)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        memberRepository.findByEmail(email)
                .ifPresent(i -> {throw new CustomException(ErrorCode.MEMBER_ALREADY_MAPPED);});
        String invitationKey = "invitation:" + trainer.getId() + ":" + email;
        //TODO: 프론트와 상의 후 초대링크 페이지 url 수정하기
        String invitationLink = "http://www.temp.com?trainerId=" + trainer.getId() + "&email=" + email;
        sendInviteLink(email, trainer, invitationLink);
        redisService.setValuesWithTimeout(invitationKey, invitationLink, 3 * 24 * 60 * 60 * 1000); // 3days
        return MemberInviteCommandResult.from(email, trainer.getId(), invitationLink);
    }

    private void sendInviteLink(String email, Member trainer, String invitationLink) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("[건강해짐] 안녕하세요. {trainerName}님이 고객님을 초대했습니다.".replace("{trainerName}", trainer.getName())); // 메일 제목
            String text = "안녕하세요. {trainerName}님이 고객님을 초대했습니다.\n하단 링크를 통해 회원가입을 해주세요.\n{inviteLink}"
                    .replace("{trainerName}", trainer.getName())
                    .replace("{inviteLink}", "http://www.temp.com?trainerId="+trainer.getId()+"&email="+email);
            mimeMessageHelper.setText(text, false); // 메일 본문 내용, HTML 여부
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(MAIL_SEND_ERROR);
        }
    }
}
