package com.tobe.healthy.member.domain.dto.in;

import lombok.Data;

@Data
public class EmailChangeCommand {
    private String email;
    private String authNumber;
}
