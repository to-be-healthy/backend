package com.tobe.healthy.workout.domain.dto.in;

import com.tobe.healthy.workout.domain.dto.CompletedExerciseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HistoryAddCommand {

    @Schema(description = "게시글 내용" , example = "오운완~!!")
    private String content;

    @Schema(description = "완료한 운동 목록")
    @NotEmpty(message = "운동을 추가해 주세요.")
    private List<CompletedExerciseDto> completedExercises = new ArrayList<>();

    private List<RegisterFile> files = new ArrayList<>();

    private boolean viewMySelf;

}
