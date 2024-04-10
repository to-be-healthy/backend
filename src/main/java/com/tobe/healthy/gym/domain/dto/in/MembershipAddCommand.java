package com.tobe.healthy.gym.domain.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MembershipAddCommand {

    @Schema(description = "헬스장 번호", example = "1")
    @NotEmpty(message = "헬스장 번호를 입력해주세요.")
    private Long gymId;

    @Schema(description = "학생 번호", example = "1")
    @NotEmpty(message = "학생 번호를 입력해주세요.")
    private Long memberId;

//    @Schema(description = "헬스장 이용권 시작날짜", example = "2024-02-01")
//    private LocalDate gymStartDt;
//
//    @Schema(description = "헬스장 이용권 종료날짜", example = "2024-05-01")
//    private LocalDate gymEndDt;

    @Builder
    public MembershipAddCommand(Long gymId, Long memberId){
        this.gymId = gymId;
        this.memberId = memberId;
//        this.gymStartDt = gymStartDt;
//        this.gymEndDt = gymEndDt;
    }

}



