package com.tobe.healthy.workout.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tobe.healthy.file.domain.dto.WorkoutHistoryFileDto;
import com.tobe.healthy.member.domain.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutHistoryDto {

    private Long workoutHistoryId;
    private String content;
    private MemberDto member;
    private List<WorkoutHistoryFileDto> files = new ArrayList<>();

    @JsonIgnore
    private List<MultipartFile> multipartFiles = new ArrayList<>();

    public static WorkoutHistoryDto create(String content, MemberDto memberDto, List<MultipartFile> files){
        return WorkoutHistoryDto.builder()
                .content(content)
                .member(memberDto)
                .multipartFiles(files)
                .build();
    }

}
