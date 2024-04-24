package com.tobe.healthy.diet.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.diet.domain.dto.DietDto;
import com.tobe.healthy.diet.domain.dto.in.DietAddCommand;
import com.tobe.healthy.diet.domain.entity.Diet;
import com.tobe.healthy.diet.domain.entity.DietLike;
import com.tobe.healthy.diet.domain.entity.DietLikePK;
import com.tobe.healthy.diet.repository.DietLikeRepository;
import com.tobe.healthy.diet.repository.DietRepository;
import com.tobe.healthy.file.application.FileService;
import com.tobe.healthy.file.domain.dto.DietFileDto;
import com.tobe.healthy.file.domain.dto.WorkoutHistoryFileDto;
import com.tobe.healthy.file.domain.entity.DietFile;
import com.tobe.healthy.file.domain.entity.DietType;
import com.tobe.healthy.file.domain.entity.WorkoutHistoryFile;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import com.tobe.healthy.workout.domain.dto.out.WorkoutHistoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tobe.healthy.config.error.ErrorCode.*;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DietService {

    private final DietRepository dietRepository;
    private final DietLikeRepository dietLikeRepository;
    private final TrainerMemberMappingRepository mappingRepository;
    private final FileService fileService;

    public List<DietDto> getDiet(Long memberId, Pageable pageable, String searchDate) {
        Page<Diet> diets = dietRepository.getDietOfMonth(memberId, pageable, searchDate);
        List<DietDto> dietDtos = diets.map(DietDto::from).stream().toList();
        List<Long> ids = dietDtos.stream().map(DietDto::getDietId).collect(Collectors.toList());
        dietDtos = setDietFile(dietDtos, ids);
        return dietDtos.isEmpty() ? null : dietDtos;
    }

    public void likeDiet(Member member, Long dietId) {
        Diet diet = dietRepository.findByDietIdAndDelYnFalse(dietId)
                .orElseThrow(() -> new CustomException(DIET_NOT_FOUND));
        DietLikePK likePk = DietLikePK.create(diet, member);
        dietLikeRepository.findById(likePk).ifPresent(i -> {throw new CustomException(LIKE_ALREADY_EXISTS);});
        dietLikeRepository.save(DietLike.from(likePk));
        diet.updateLikeCnt(dietLikeRepository.getLikeCnt(diet.getDietId()));
    }

    public void deleteLikeDiet(Member member, Long dietId) {
        Diet diet = dietRepository.findByDietIdAndDelYnFalse(dietId)
                .orElseThrow(() -> new CustomException(DIET_NOT_FOUND));
        dietLikeRepository.delete(DietLike.from(DietLikePK.create(diet, member)));
        diet.updateLikeCnt(dietLikeRepository.getLikeCnt(diet.getDietId()));
    }

    public DietDto addDiet(Member member, DietAddCommand command) {
        Long memberId = member.getId();
        DietType requestType = command.getType();
        MultipartFile requestFile = command.getFile();
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0));
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59));
        Diet diet = dietRepository.findTop1ByCreateAtToday(memberId, start, end);

        TrainerMemberMapping mapping = mappingRepository.findTop1ByMemberIdOrderByCreatedAtDesc(memberId).orElse(null);
        if(ObjectUtils.isEmpty(diet)) diet = dietRepository.save(Diet.create(member, mapping == null ? null : mapping.getTrainer()));

        diet.deleteFile(requestType);
        if(!ObjectUtils.isEmpty(diet.getDietFiles())) {
            diet.getDietFiles().stream()
                    .filter(f -> requestType.equals(f.getType()))
                    .forEach(file -> fileService.deleteFile(file.getFileName()));
        }

        if(!ObjectUtils.isEmpty(requestFile)){
            fileService.uploadDietFile(diet, requestType, requestFile);
        }

        diet.changeFast(command);
        return setDietFile(DietDto.from(diet), List.of(diet.getDietId()));
    }

    public DietDto getDietDetail(Long dietId) {
        Diet diet = dietRepository.findByDietIdAndDelYnFalse(dietId)
                .orElseThrow(() -> new CustomException(DIET_NOT_FOUND));
        return setDietFile(DietDto.from(diet), List.of(dietId));
    }

    private DietDto setDietFile(DietDto dietDto, List<Long> ids) {
        List<DietFile> files = dietRepository.getDietFile(ids);
        List<DietFileDto> filesDto = files.stream().map(DietFileDto::from).collect(Collectors.toList());
        dietDto.setDietFiles(filesDto);
        return dietDto;
    }

    private List<DietDto> setDietFile(List<DietDto> dietDtos, List<Long> ids) {
        List<DietFile> files = dietRepository.getDietFile(ids);
        return dietDtos.stream().map(d -> {
            List<DietFileDto> thisFiles = files.stream().map(DietFileDto::from)
                    .filter(f -> f.getDietId().equals(d.getDietId())).collect(Collectors.toList());
            d.setDietFiles(thisFiles);
            return d;
        }).collect(Collectors.toList());
    }

}
