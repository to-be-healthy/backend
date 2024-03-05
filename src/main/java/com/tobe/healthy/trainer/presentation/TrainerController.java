package com.tobe.healthy.trainer.presentation;

import com.tobe.healthy.trainer.application.TrainerService;
import com.tobe.healthy.trainer.domain.dto.TrainerMemberMappingDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class TrainerController {

    private final TrainerService trainerService;

    @Operation(summary = "내 회원으로 등록하기", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "매핑ID, 트레이너ID, 회원ID를 반환한다.")
    })
    @PostMapping("/trainers/{trainerId}/members/{memberId}")
    public ResponseEntity<TrainerMemberMappingDto> addMemberOfTrainer(@PathVariable("trainerId") Long trainerId,
                                                                      @PathVariable("memberId") Long memberId) {
        return ResponseEntity.ok(trainerService.addMemberOfTrainer(trainerId, memberId));
    }

}
