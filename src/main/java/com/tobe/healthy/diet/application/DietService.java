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
import com.tobe.healthy.workout.application.FileService;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
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

import java.util.List;
import java.util.stream.Collectors;

import static com.tobe.healthy.config.error.ErrorCode.*;
import static com.tobe.healthy.diet.domain.entity.DietType.*;
import static com.tobe.healthy.member.domain.entity.MemberType.TRAINER;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DietService {

    private final DietRepository dietRepository;
    private final DietLikeRepository dietLikeRepository;
    private final TrainerMemberMappingRepository mappingRepository;
    private final FileService fileService;
    private final MemberRepository memberRepository;

    public DietDto getDietCreatedAtToday(Long memberId) {
        Diet diet = dietRepository.getDietCreatedAtToday(memberId);
        if (diet == null) return null;
        List<Long> ids = List.of(diet.getDietId());
        return setDietFile(DietDto.from(diet), ids);
    }

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
        dietLikeRepository.findById(likePk).ifPresent(i -> {
            throw new CustomException(LIKE_ALREADY_EXISTS);
        });
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

        Diet diet = dietRepository.findTop1ByCreateAtToday(memberId);

        TrainerMemberMapping mapping = mappingRepository.findTop1ByMemberIdOrderByCreatedAtDesc(memberId).orElse(null);
        Member trainer = mapping == null ? null : mapping.getTrainer();
        if (ObjectUtils.isEmpty(diet)) diet = dietRepository.save(Diet.create(member, trainer));

        diet.deleteFile(requestType);
        if (!ObjectUtils.isEmpty(diet.getDietFiles())) {
            diet.getDietFiles().stream()
                    .filter(f -> requestType.equals(f.getType()))
                    .forEach(file -> fileService.deleteDietFile(getFileName(file.getFileUrl())));
        }

        if (!ObjectUtils.isEmpty(requestFile)) {
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
        diet.getDietFiles().forEach(file -> fileService.deleteDietFile(getFileName(file.getFileUrl())));
    }

    public DietDto updateDiet(Member member, Long dietId, DietUpdateCommand command) {
        Diet diet = dietRepository.findByDietIdAndMemberIdAndDelYnFalse(dietId, member.getId())
                .orElseThrow(() -> new CustomException(DIET_NOT_FOUND));

        diet.changeFastBreakfast(command.isBreakfastFast());
        diet.changeFastLunch(command.isLunchFast());
        diet.changeFastDinner(command.isDinnerFast());

        diet.deleteFiles();
        diet.getDietFiles().forEach(file -> fileService.deleteDietFile(getFileName(file.getFileUrl())));
        if (!ObjectUtils.isEmpty(command.getBreakfastFile()))
            fileService.uploadDietFile(diet, BREAKFAST, command.getBreakfastFile());
        if (!ObjectUtils.isEmpty(command.getLunchFile()))
            fileService.uploadDietFile(diet, LUNCH, command.getLunchFile());
        if (!ObjectUtils.isEmpty(command.getDinnerFile()))
            fileService.uploadDietFile(diet, DINNER, command.getDinnerFile());

        return setDietFile(DietDto.from(diet), List.of(diet.getDietId()));
    }

    public CustomPaging<DietDto> getDietMyTrainer(Long studentId, Pageable pageable, String searchDate) {
        TrainerMemberMapping mapping = mappingRepository.findTop1ByMemberIdOrderByCreatedAtDesc(studentId)
                .orElseThrow(() -> new CustomException(TRAINER_NOT_MAPPED));
        Member trainer = memberRepository.findByIdAndMemberTypeAndDelYnFalse(mapping.getTrainer().getId(), TRAINER)
                .orElseThrow(() -> new CustomException(TRAINER_NOT_FOUND));
        return getDietByTrainer(trainer.getId(), pageable, searchDate);
    }

    public CustomPaging<DietDto> getDietByTrainer(Long trainerId, Pageable pageable, String searchDate) {
        Member trainer = memberRepository.findByIdAndMemberTypeAndDelYnFalse(trainerId, TRAINER)
                .orElseThrow(() -> new CustomException(TRAINER_NOT_FOUND));
        Page<Diet> pageDtos = dietRepository.getDietByTrainer(trainer, pageable, searchDate);
        List<DietDto> dietDtos = pageDtos.map(DietDto::from).stream().toList();
        List<Long> ids = dietDtos.stream().map(DietDto::getDietId).collect(Collectors.toList());
        List<DietDto> content = setDietFile(dietDtos, ids);
        return new CustomPaging(content, pageDtos.getPageable().getPageNumber(),
                pageDtos.getPageable().getPageSize(), pageDtos.getTotalPages(), pageDtos.getTotalElements(), pageDtos.isLast());
    }

    private String getFileName(String url) {
        String[] arr = url.split("/");
        return arr[arr.length - 1];
    }

}
