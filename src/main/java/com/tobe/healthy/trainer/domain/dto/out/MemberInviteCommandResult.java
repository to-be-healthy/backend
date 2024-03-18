package com.tobe.healthy.trainer.domain.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MemberInviteCommandResult {

    private String email;
    private Long trainerId;
    private String invitationLink;

    public static MemberInviteCommandResult from(String email, Long trainerId, String invitationLink){
        return MemberInviteCommandResult.builder()
                .email(email)
                .trainerId(trainerId)
                .invitationLink(invitationLink)
                .build();
    }

}
