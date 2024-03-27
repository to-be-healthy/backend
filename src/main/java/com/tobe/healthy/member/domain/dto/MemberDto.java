package com.tobe.healthy.member.domain.dto;

import com.tobe.healthy.file.domain.dto.ProfileDto;
import com.tobe.healthy.gym.domain.dto.GymDto;
import com.tobe.healthy.gym.domain.entity.Gym;
import com.tobe.healthy.member.domain.entity.AlarmStatus;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberType;
import com.tobe.healthy.member.domain.entity.SocialType;
import lombok.Builder;
import lombok.Data;



@Data
@Builder
public class MemberDto {

    private Long id;
    private String userId;
    private String email;
    private String name;
    private boolean delYn;

    private ProfileDto profile;
    private MemberType memberType;
    private AlarmStatus pushAlarmStatus;
    private AlarmStatus feedbackAlarmStatus;
    private GymDto gym;
    private SocialType socialType;

    public static MemberDto from(Member member){
        MemberDtoBuilder builder = MemberDto.builder()
                .id(member.getId())
                .userId(member.getUserId())
                .email(member.getEmail())
                .name(member.getName())
                .delYn(member.isDelYn())
                .memberType(member.getMemberType())
                .pushAlarmStatus(member.getPushAlarmStatus())
                .feedbackAlarmStatus(member.getFeedbackAlarmStatus())
                .socialType(member.getSocialType());

        if(member.getProfileId() != null){
            builder.profile(ProfileDto.from(member.getProfileId()));
        }
        return builder.build();
    }

    public static MemberDto create(Member member, Gym gym){
        return MemberDto.builder()
                .id(member.getId())
                .userId(member.getUserId())
                .email(member.getEmail())
                .name(member.getName())
                .delYn(member.isDelYn())
                .profile(ProfileDto.from(member.getProfileId()))
                .memberType(member.getMemberType())
                .pushAlarmStatus(member.getPushAlarmStatus())
                .feedbackAlarmStatus(member.getFeedbackAlarmStatus())
                .gym(GymDto.from(gym))
                .socialType(member.getSocialType())
                .build();
    }

}