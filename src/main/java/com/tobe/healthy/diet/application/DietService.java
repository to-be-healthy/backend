package com.tobe.healthy.diet.application;

import com.tobe.healthy.common.CustomPaging;
import com.tobe.healthy.common.redis.RedisService;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.diet.domain.dto.DietDetailDto;
import com.tobe.healthy.diet.domain.dto.DietDto;
import com.tobe.healthy.diet.domain.dto.DietFileDto;
import com.tobe.healthy.diet.domain.dto.in.DietAddCommand;
import com.tobe.healthy.diet.domain.dto.in.DietAddCommandAtHome;
import com.tobe.healthy.diet.domain.dto.in.DietUpdateCommand;
import com.tobe.healthy.diet.domain.dto.out.DietUploadDaysResult;
import com.tobe.healthy.diet.domain.entity.*;
import com.tobe.healthy.diet.repository.DietCommentRepository;
import com.tobe.healthy.diet.repository.DietFileRepository;
import com.tobe.healthy.diet.repository.DietLikeRepository;
import com.tobe.healthy.diet.repository.DietRepository;
import com.tobe.healthy.workout.application.FileService;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import com.tobe.healthy.workout.domain.dto.in.RegisterFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tobe.healthy.common.Utils.CDN_DOMAIN;
import static com.tobe.healthy.common.Utils.S3_DOMAIN;
import static com.tobe.healthy.common.redis.RedisKeyPrefix.TEMP_FILE_URI;
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
    private final DietFileRepository dietFileRepository;
    private final TrainerMemberMappingRepository mappingRepository;
    private final FileService fileService;
    private final MemberRepository memberRepository;
    private final RedisService redisService;
    private final DietCommentRepository commentRepository;

    public DietDto getTodayDiet(Long memberId) {
        Diet diet = dietRepository.getTodayDiet(memberId);
        if (diet == null) return new DietDto();
        DietDto dietDto = DietDto.from(diet);
        dietLikeRepository.findById(DietLikePK.create(diet, diet.getMember()))
                .ifPresent(i -> {dietDto.setLiked(true);});
        setDietFile(dietDto, List.of(diet.getDietId()));
        return dietDto;
    }

    public CustomPaging<DietDto> getDiet(Long loginMemberId, Long memberId, Pageable pageable, String searchDate) {
        Page<DietDto> pageDtos = dietRepository.getDietOfMonth(loginMemberId, memberId, pageable, searchDate);
        List<DietDto> dietDtos = pageDtos.stream().toList();
        List<Long> ids = dietDtos.stream().map(DietDto::getDietId).collect(Collectors.toList());
        setDietFile(dietDtos, ids);
        return new CustomPaging<>(dietDtos, pageDtos.getPageable().getPageNumber(),
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

    public DietDto addDietAtHome(Member member, DietAddCommandAtHome command) {
        if(!LocalDate.now().toString().equals(command.getEatDate())){
            throw new CustomException(DATE_NOT_VALID);
        }
        Long memberId = member.getId();
        DietType requestType = command.getType();
        String originalFileUrl = command.getFile();

        Diet diet = dietRepository.getTodayDiet(memberId);

        Member trainer = getMappedTrainer(memberId);
        if (ObjectUtils.isEmpty(diet)) diet = dietRepository.save(Diet.create(member, trainer));

        diet.changeTrainer(trainer);
        diet.deleteFile(requestType);
        if (!ObjectUtils.isEmpty(diet.getDietFiles())) {
            diet.getDietFiles().stream()
                    .filter(f -> requestType.equals(f.getType()))
                    .forEach(file -> fileService.deleteDietFile(getFileName(file.getFileUrl())));
        }

        if (!command.isFast() && !ObjectUtils.isEmpty(originalFileUrl)){
            dietFileRepository.save(DietFiles.create(diet, originalFileUrl, command.getType()));
            redisService.deleteValues(TEMP_FILE_URI.getDescription() + originalFileUrl);
        }

        diet.changeEatDate(command.getEatDate());
        diet.changeFast(command.getType(), command.isFast());
        DietDto dietDto = DietDto.from(diet);
        setDietFile(dietDto, List.of(diet.getDietId()));

        if(isClean(dietDto) && commentNotExists(diet)) diet.deleteDiet();
        return dietDto;
    }

    private boolean commentNotExists(Diet diet) {
        return commentRepository.countByDietAndDelYnFalse(diet) == 0L;
    }

    private boolean isClean(DietDto dietDto) {
        DietDetailDto breakfast = dietDto.getBreakfast();
        DietDetailDto lunch = dietDto.getLunch();
        DietDetailDto dinner = dietDto.getDinner();
        return (!breakfast.getFast() && breakfast.getDietFile()==null)
                && (!lunch.getFast() && lunch.getDietFile()==null)
                && (!dinner.getFast() && dinner.getDietFile()==null);
    }

    public DietDto getDietDetail(Long loginMemberId, Long dietId) {
        DietDto dietDto = dietRepository.getDietById(loginMemberId, dietId);
        if(dietDto == null) throw new CustomException(DIET_NOT_FOUND);
        setDietFile(dietDto, List.of(dietId));
        return dietDto;
    }

    private void setDietFile(DietDto dietDto, List<Long> ids) {
        List<DietFiles> files = dietRepository.getDietFile(ids);
        List<DietFileDto> filesDto = files.stream().map(DietFileDto::from).collect(Collectors.toList());
        dietDto.setDietFiles(filesDto);
    }

    private void setDietFile(List<DietDto> dietDtos, List<Long> ids) {
        List<DietFiles> files = dietRepository.getDietFile(ids);
        dietDtos.stream().peek(d -> {
            List<DietFileDto> thisFiles = files.stream().map(DietFileDto::from)
                    .filter(f -> f.getDietId().equals(d.getDietId())).collect(Collectors.toList());
            d.setDietFiles(thisFiles);
        }).collect(Collectors.toList());
    }

    public void deleteDiet(Member member, Long dietId) {
        Diet diet = dietRepository.findByDietIdAndMemberIdAndDelYnFalse(dietId, member.getId())
                .orElseThrow(() -> new CustomException(DIET_NOT_FOUND));
        diet.deleteDiet();
        dietLikeRepository.deleteLikeByDietId(dietId);
        diet.getDietFiles().forEach(file -> fileService.deleteDietFile(getFileName(file.getFileUrl())));
    }

    public DietDto addDiet(Member member, DietAddCommand command) {
        List<String> uploadDays = dietRepository.getDietUploadDays(member.getId(), null, null);
        if(uploadDays.contains(command.getEatDate())) throw new CustomException(DIET_ALREADY_EXISTS);

        Member trainer = getMappedTrainer(member.getId());
        Diet diet = dietRepository.save(Diet.create(member, trainer, command));
        uploadNewFiles(diet, command);
        DietDto dietDto = DietDto.from(diet);
        setDietFile(dietDto, List.of(diet.getDietId()));
        return dietDto;
    }

    private @Nullable Member getMappedTrainer(Long memberId) {
        TrainerMemberMapping mapping = mappingRepository.findTop1ByMemberIdOrderByCreatedAtDesc(memberId).orElse(null);
        return mapping == null ? null : mapping.getTrainer();
    }

    public DietDto updateDiet(Member member, Long dietId, DietUpdateCommand command) {
        Diet diet = dietRepository.findByDietIdAndMemberIdAndDelYnFalse(dietId, member.getId())
                .orElseThrow(() -> new CustomException(DIET_NOT_FOUND));

        diet.changeTrainer(getMappedTrainer(member.getId()));
        diet.changeFast(command);
        deleteOldFiles(diet, command);
        uploadNewFiles(diet, command);

        DietDto dietDto = DietDto.from(diet);
        setDietFile(dietDto, List.of(diet.getDietId()));
        if(isClean(dietDto)) throw new CustomException(DIET_NOT_VALID);
        return dietDto;
    }

    private void uploadNewFiles(Diet diet, DietUpdateCommand command) {
        //아침 파일
        if (!command.isBreakfastFast() && !ObjectUtils.isEmpty(command.getBreakfastFile())){
            String oldSavedFileName = command.getBreakfastFile().replace(S3_DOMAIN, "");
            RegisterFile result = fileService.moveDirTempToOrigin("diet/", oldSavedFileName);
            dietFileRepository.save(DietFiles.create(diet, result.getFileUrl(), BREAKFAST));
        }else{
            List<DietFiles> files = diet.getDietFiles().stream().filter(f -> BREAKFAST.equals(f.getType())).toList();
            if(!ObjectUtils.isEmpty(files)) fileService.deleteDietFile(getFileName(files.get(0).getFileUrl()));
        }

        //점심 파일
        if (!command.isLunchFast() && !ObjectUtils.isEmpty(command.getLunchFile())){
            String oldSavedFileName = command.getLunchFile().replace(S3_DOMAIN, "");
            RegisterFile result = fileService.moveDirTempToOrigin("diet/", oldSavedFileName);
            dietFileRepository.save(DietFiles.create(diet, result.getFileUrl(), LUNCH));
        }else{
            List<DietFiles> files = diet.getDietFiles().stream().filter(f -> LUNCH.equals(f.getType())).toList();
            if(!ObjectUtils.isEmpty(files)) fileService.deleteDietFile(getFileName(files.get(0).getFileUrl()));
        }

        //저녁 파일
        if (!command.isDinnerFast() && !ObjectUtils.isEmpty(command.getDinnerFile())){
            String oldSavedFileName = command.getDinnerFile().replace(S3_DOMAIN, "");
            RegisterFile result = fileService.moveDirTempToOrigin("diet/", oldSavedFileName);
            dietFileRepository.save(DietFiles.create(diet, result.getFileUrl(), DINNER));
        }else{
            List<DietFiles> files = diet.getDietFiles().stream().filter(f -> DINNER.equals(f.getType())).toList();
            if(!ObjectUtils.isEmpty(files)) fileService.deleteDietFile(getFileName(files.get(0).getFileUrl()));
        }
    }

    private void deleteOldFiles(Diet diet, DietUpdateCommand command) {
        Set<String> oldFileUrlSet = diet.getDietFiles().stream()
                .filter(f -> !f.getDelYn())
                .map(f -> f.getFileUrl().replace(CDN_DOMAIN, "")).collect(Collectors.toSet());
        oldFileUrlSet.remove(command.getBreakfastFile());
        oldFileUrlSet.remove(command.getLunchFile());
        oldFileUrlSet.remove(command.getDinnerFile());
        Set<DietFiles> deleteFilesSet = diet.getDietFiles().stream()
                .filter(f -> oldFileUrlSet.contains(f.getFileUrl())).collect(Collectors.toSet());

        diet.deleteFiles();
        diet.getDietFiles().stream()
                .filter(deleteFilesSet::contains)
                .forEach(file -> fileService.deleteDietFile(getFileName(file.getFileUrl())));
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
        List<DietDto> dietDtos = setFeedbackChecked(trainerId, pageDtos);
        List<Long> ids = dietDtos.stream().map(DietDto::getDietId).collect(Collectors.toList());
        setDietFile(dietDtos, ids);
        return new CustomPaging<>(dietDtos, pageDtos.getPageable().getPageNumber(),
                pageDtos.getPageable().getPageSize(), pageDtos.getTotalPages(), pageDtos.getTotalElements(), pageDtos.isLast());
    }

    private List<DietDto> setFeedbackChecked(Long trainerId, Page<Diet> pageDtos) {
        List<DietDto> dietDtos = pageDtos.map(diet -> {
            DietDto dto = DietDto.from(diet);
            Long feedbackCnt = commentRepository.countByDietAndMemberIdAndDelYnFalse(diet, trainerId);
            dto.setFeedbackChecked(0 < feedbackCnt);
            return dto;
        }).stream().toList();
        return dietDtos;
    }

    private String getFileName(String url) {
        String[] arr = url.split("/");
        return arr[arr.length - 1];
    }

    public DietUploadDaysResult getDietUploadDays(Long memberId, LocalDate startDate, LocalDate endDate) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        List<String> days = dietRepository.getDietUploadDays(memberId, startDate, endDate);
        return DietUploadDaysResult.create(member.getDietNoticeStatus(), days);
    }

}
