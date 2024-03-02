package com.tobe.healthy.workout.domain.dto.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutHistoryAddCommand {

    @NotEmpty(message = "내용을 입력해 주세요.")
    private String content;

    @NotNull(message = "운동을 추가해 주세요.")
    private List<WorkoutForm> workoutFormList = new ArrayList<>();

    @NotEmpty(message = "사진을 추가해 주세요.")
    private List<MultipartFile> files = new ArrayList<>();

}
