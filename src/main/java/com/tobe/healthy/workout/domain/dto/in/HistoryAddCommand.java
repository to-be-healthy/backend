package com.tobe.healthy.workout.domain.dto.in;

import com.tobe.healthy.file.RegisterFile;
import com.tobe.healthy.workout.domain.dto.CompletedExerciseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryAddCommand {

    @Schema(description = "게시글 내용" , example = "오운완~!!")
    @NotEmpty(message = "내용을 입력해 주세요.")
    private String content;

    @Schema(description = "완료한 운동 목록")
    @NotNull(message = "운동을 추가해 주세요.")
    private List<CompletedExerciseDto> completedExercises = new ArrayList<>();

    @NotEmpty(message = "사진을 추가해 주세요.")
    private List<RegisterFile> files = new ArrayList<>();

}
