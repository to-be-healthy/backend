package com.tobe.healthy.member.domain.dto.in;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CommandChangeEmail {
    private String email;
    private String emailKey;
}
