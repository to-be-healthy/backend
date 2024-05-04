package com.tobe.healthy.diet.application;

import com.tobe.healthy.common.CustomPaging;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.diet.domain.dto.DietDto;
import com.tobe.healthy.diet.domain.dto.DietFileDto;
import com.tobe.healthy.diet.domain.dto.in.DietAddCommand;
import com.tobe.healthy.diet.domain.dto.in.DietUpdateCommand;
import com.tobe.healthy.diet.domain.entity.*;
import com.tobe.healthy.diet.repository.DietLikeRepository;
import com.tobe.healthy.diet.repository.DietRepository;
import com.tobe.healthy.file.FileService;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
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
import java.util.stream.Collectors;

import static com.tobe.healthy.config.error.ErrorCode.DIET_NOT_FOUND;
import static com.tobe.healthy.config.error.ErrorCode.LIKE_ALREADY_EXISTS;
import static com.tobe.healthy.diet.domain.entity.DietType.*;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DietService {

    private final DietRepository dietRepository;
    private final DietLikeRepository dietLikeRepository;
    private final TrainerMemberMappingRepository mappingRepository;
    private final FileService fileService;

    public CustomPaging<DietDto> getDiet(Long memberId, Pageable pageable, String searchDate) {
        Page<Diet> pageDtos = dietRepository.getDietOfMonth(memberId, pageable, searchDate);
        List<DietDto> dietDtos = pageDtos.map(DietDto::from).stream().toList();
        List<Long> ids = dietDtos.stream().map(DietDto::getDietId).collect(Collectors.toList());
        List<DietDto> content = setDietFile(dietDtos, ids);
        return new CustomPaging(content, pageDtos.getPageable().getPageNumber(),
                pageDtos.getPageable().getPageSize(), pageDtos.getTotalPages(), pageDtos.getTotalElements(), pageDtos.isLast());
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

        diet.changeFast(command.getType(), command.isFast());
        return setDietFile(DietDto.from(diet), List.of(diet.getDietId()));
    }

    public DietDto getDietDetail(Long dietId) {
        Diet diet = dietRepository.findByDietIdAndDelYnFalse(dietId)
                .orElseThrow(() -> new CustomException(DIET_NOT_FOUND));
        return setDietFile(DietDto.from(diet), List.of(dietId));
    }

    private DietDto setDietFile(DietDto dietDto, List<Long> ids) {
        List<DietFiles> files = dietRepository.getDietFile(ids);
        List<DietFileDto> filesDto = files.stream().map(DietFileDto::from).collect(Collectors.toList());
        dietDto.setDietFiles(filesDto);
        return dietDto;
    }

    private List<DietDto> setDietFile(List<DietDto> dietDtos, List<Long> ids) {
        List<DietFiles> files = dietRepository.getDietFile(ids);
        return dietDtos.stream().map(d -> {
            List<DietFileDto> thisFiles = files.stream().map(DietFileDto::from)
                    .filter(f -> f.getDietId().equals(d.getDietId())).collect(Collectors.toList());
            d.setDietFiles(thisFiles);
            return d;
        }).collect(Collectors.toList());
    }

    public void deleteDiet(Member member, Long dietId) {
        Diet diet = dietRepository.findByDietIdAndMemberIdAndDelYnFalse(dietId, member.getId())
                .orElseThrow(() -> new CustomException(DIET_NOT_FOUND));
        diet.deleteDiet();
        dietLikeRepository.deleteLikeByDietId(dietId);
        diet.getDietFiles().forEach(file -> fileService.deleteFile(file.getFileName()));
    }

    public DietDto updateDiet(Member member, Long dietId, DietUpdateCommand command) {
        Diet diet = dietRepository.findByDietIdAndMemberIdAndDelYnFalse(dietId, member.getId())
                .orElseThrow(() -> new CustomException(DIET_NOT_FOUND));

        diet.changeFastBreakfast(command.isBreakfastFast());
        diet.changeFastLunch(command.isLunchFast());
        diet.changeFastDinner(command.isDinnerFast());

        diet.deleteFiles();
        diet.getDietFiles().forEach(file -> fileService.deleteFile(file.getFileName()));
        if(!ObjectUtils.isEmpty(command.getBreakfastFile())) fileService.uploadDietFile(diet, BREAKFAST, command.getBreakfastFile());
        if(!ObjectUtils.isEmpty(command.getLunchFile())) fileService.uploadDietFile(diet, LUNCH, command.getLunchFile());
        if(!ObjectUtils.isEmpty(command.getDinnerFile())) fileService.uploadDietFile(diet, DINNER, command.getDinnerFile());

        return setDietFile(DietDto.from(diet), List.of(diet.getDietId()));
    }
}
