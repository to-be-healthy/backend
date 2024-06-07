package com.tobe.healthy.member.domain.dto.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommandChangeName {
    @NotEmpty
    private String name;
}
