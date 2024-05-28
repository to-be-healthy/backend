package com.tobe.healthy.member.domain.dto.out;

import com.tobe.healthy.member.domain.dto.MemberDto;
import com.tobe.healthy.member.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
@AllArgsConstructor
@Builder
public class InvitationMappingResult {

    private MemberDto trainer;
    private String name;
    private int lessonCnt;

    public static InvitationMappingResult create(Member member, String name, int lessonCnt){
        return InvitationMappingResult.builder()
                .trainer(MemberDto.from(member))
                .name(name)
                .lessonCnt(lessonCnt)
                .build();
    }
}
