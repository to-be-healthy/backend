package com.tobe.healthy.member.domain.dto.out;

import com.tobe.healthy.gym.domain.dto.GymDto;
import com.tobe.healthy.member.domain.dto.MemberDto;
import com.tobe.healthy.member.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class InvitationMappingResult {

    private MemberDto trainer;
    private String name;
    private int lessonNum;
    private int age;
    private int height;
    private int weight;

    public static InvitationMappingResult create(Member member, String name, int lessonNum, int age, int height, int weight){
        return InvitationMappingResult.builder()
                .trainer(MemberDto.from(member))
                .name(name)
                .lessonNum(lessonNum)
                .age(age)
                .height(height)
                .weight(weight)
                .build();
    }
}
