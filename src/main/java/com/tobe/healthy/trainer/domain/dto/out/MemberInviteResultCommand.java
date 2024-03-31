package com.tobe.healthy.trainer.domain.dto.out;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MemberInviteResultCommand {

    @Schema(description = "트레이너/회원정보 매핑 uuid")
    private String uuid;

    @Schema(description = "초대링크")
    private String invitationLink;

    public MemberInviteResultCommand(String uuid, String invitationLink) {
        this.uuid = uuid;
        this.invitationLink = invitationLink;
    }


}
