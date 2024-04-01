package com.tobe.healthy.member.domain.dto.out;

import com.tobe.healthy.gym.domain.dto.GymDto;
import com.tobe.healthy.member.domain.dto.MemberDto;
import com.tobe.healthy.member.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class InvitationMappingResult {

    private MemberDto trainer;
    private String name;
    private int lessonCnt;
    private LocalDate gymStartDt;
    private LocalDate gymEndDt;

    public static InvitationMappingResult create(Member member, String name, int lessonCnt, LocalDate gymStartDt, LocalDate gymEndDt){
        return InvitationMappingResult.builder()
                .trainer(MemberDto.from(member))
                .name(name)
                .lessonCnt(lessonCnt)
                .gymStartDt(gymStartDt)
                .gymEndDt(gymEndDt)
                .build();
    }
}
