package com.tobe.healthy.trainer.application;

import com.tobe.healthy.common.redis.RedisKeyPrefix;
import com.tobe.healthy.common.redis.RedisService;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.course.application.CourseService;
import com.tobe.healthy.course.domain.dto.CourseDto;
import com.tobe.healthy.course.domain.dto.in.CourseAddCommand;
import com.tobe.healthy.course.domain.entity.Course;
import com.tobe.healthy.course.repository.CourseRepository;
import com.tobe.healthy.diet.application.DietService;
import com.tobe.healthy.diet.domain.dto.DietDto;
import com.tobe.healthy.gym.domain.dto.out.GymDto;
import com.tobe.healthy.member.domain.dto.MemberDto;
import com.tobe.healthy.member.domain.dto.out.MemberDetailResult;
import com.tobe.healthy.member.domain.dto.out.MemberInTeamResult;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberType;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.point.domain.dto.out.PointDto;
import com.tobe.healthy.point.domain.dto.out.RankDto;
import com.tobe.healthy.point.repository.PointRepository;
import com.tobe.healthy.trainer.domain.dto.TrainerMemberMappingDto;
import com.tobe.healthy.trainer.domain.dto.in.MemberInviteCommand;
import com.tobe.healthy.trainer.domain.dto.in.MemberLessonCommand;
import com.tobe.healthy.trainer.domain.dto.out.MemberInviteResultCommand;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.tobe.healthy.config.error.ErrorCode.*;
import static com.tobe.healthy.member.domain.entity.MemberType.STUDENT;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TrainerService {

    private final RedisService redisService;
    private final MemberRepository memberRepository;
    private final TrainerMemberMappingRepository mappingRepository;
    private final DietService dietService;
    private final CourseService courseService;
    private final CourseRepository courseRepository;
    private final PointRepository pointRepository;

    private static final int ONE_DAY = 24 * 60 * 60 * 1000;


    public TrainerMemberMappingDto addStudentOfTrainer(Long trainerId, Long memberId, MemberLessonCommand command) {
        TrainerMemberMappingDto mappingDto = mappingMemberAndTrainer(trainerId, memberId);
        courseService.addCourse(trainerId, CourseAddCommand.create(memberId, command.getLessonCnt()));
        return mappingDto;
    }

    public TrainerMemberMappingDto mappingMemberAndTrainer(Long trainerId, Long memberId) {
        Member trainer = memberRepository.findByIdAndMemberTypeAndDelYnFalse(trainerId, MemberType.TRAINER)
                .orElseThrow(() -> new CustomException(TRAINER_NOT_FOUND));
        Member member = memberRepository.findByIdAndMemberTypeAndDelYnFalse(memberId, MemberType.STUDENT)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        mappingRepository.findByTrainerIdAndMemberId(trainerId, memberId)
                .ifPresent(i -> {throw new CustomException(MEMBER_ALREADY_MAPPED);});

        mappingRepository.deleteByMemberId(memberId);
        mappingRepository.flush();
        TrainerMemberMapping mapping = TrainerMemberMapping.create(trainer, member);
        mappingRepository.save(mapping);
        member.registerGym(trainer.getGym());
        return TrainerMemberMappingDto.from(mapping);
    }

    public MemberInviteResultCommand inviteMember(MemberInviteCommand command, Member trainer) {
        memberRepository.findByIdAndMemberTypeAndDelYnFalse(trainer.getId(), MemberType.TRAINER)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        String name = command.getName();
        int lessonCnt = command.getLessonCnt();

        String uuid = System.currentTimeMillis() + "-" + UUID.randomUUID();
        String invitationKey = RedisKeyPrefix.INVITATION.getDescription() + uuid;
        String invitationLink = "https://www.to-be-healthy.site/invite?uuid=" + uuid;

        Map<String, String> invitedMapping = new HashMap<>() {{
            put("trainerId", trainer.getId().toString());
            put("name", name);
            put("lessonCnt", String.valueOf(lessonCnt));
        }};
        redisService.setValuesWithTimeout(invitationKey, JSONObject.toJSONString(invitedMapping), ONE_DAY); // 1days
        return new MemberInviteResultCommand(uuid, invitationLink);
    }

    public List<MemberInTeamResult> findAllMyMemberInTeam(Long trainerId, String searchValue, String sortValue, Pageable pageable) {
        List<MemberInTeamResult> members = memberRepository.findAllMyMemberInTeam(trainerId, searchValue, sortValue, pageable);
        return members.isEmpty() ? null : members;
    }

    public List<MemberDto> findAllUnattachedMembers(Member trainer, String searchValue, String sortValue, Pageable pageable) {
        Page<Member> members = memberRepository.findAllUnattachedMembers(trainer.getGym().getId(), searchValue, sortValue, pageable);
        List<MemberDto> memberDtos = members.stream().map(MemberDto::from).collect(Collectors.toList());
        return memberDtos.isEmpty() ? null : memberDtos;
    }

    public MemberDetailResult getMemberOfTrainer(Member trainer, Long memberId) {
        memberRepository.findByIdAndMemberTypeAndDelYnFalse(trainer.getId(), MemberType.TRAINER)
                .orElseThrow(() -> new CustomException(TRAINER_NOT_FOUND));
        Member member = memberRepository.findByIdAndMemberTypeAndDelYnFalse(memberId, MemberType.STUDENT)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        //식단
        DietDto diet = dietService.getDietCreatedAtToday(memberId);
        MemberDetailResult result = memberRepository.getMemberOfTrainer(memberId);
        result.setDiet(diet);

        //수강권
        result.setCourse(courseService.getNowUsingCourse(memberId));

        //포인트
        String yyyyMM = getNowMonth();
        int monthPoint = pointRepository.getPointOfSearchMonth(memberId, yyyyMM);
        int totalPoint = pointRepository.getTotalPoint(memberId, yyyyMM);
        PointDto point = PointDto.create(yyyyMM, monthPoint, totalPoint);
        result.setPoint(point);

        //트레이너 매핑 여부
        TrainerMemberMapping mapping = mappingRepository.findTop1ByMemberIdOrderByCreatedAtDesc(memberId).orElse(null);

        //랭킹
        long totalMemberCnt = mappingRepository.countByTrainerId(trainer.getId());
        RankDto rank = RankDto.create(mapping.getRanking(), mapping.getLastMonthRanking(), (int) totalMemberCnt);
        result.setRank(rank);

        //헬스장 정보
        GymDto gym = member.getGym() == null ? null : GymDto.Companion.from(member.getGym());
        result.setGym(gym);
        return result;
    }

    private String getNowMonth() {
        return LocalDate.now().toString().substring(0, 7);
    }

    public void deleteStudentOfTrainer(Member trainer, Long memberId) {
        Member member = memberRepository.findByIdAndMemberTypeAndDelYnFalse(memberId, MemberType.STUDENT)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        mappingRepository.deleteByTrainerIdAndMemberId(trainer.getId(), member.getId());
    }
}
