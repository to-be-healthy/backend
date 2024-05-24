package com.tobe.healthy.member.domain.dto.in;

import lombok.Data;

@Data
public class CommandChangeEmail {
    private String email;
    private String emailKey;
}
