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
    private GymDto gym;
    private String email;

    public static InvitationMappingResult create(Member member, String email){
        return InvitationMappingResult.builder()
                .trainer(MemberDto.from(member))
                .gym(GymDto.from(member.getGym()))
                .email(email)
                .build();
    }
}
