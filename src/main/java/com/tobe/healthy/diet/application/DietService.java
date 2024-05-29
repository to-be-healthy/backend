package com.tobe.healthy.diet.application;

import com.tobe.healthy.common.CustomPaging;
import com.tobe.healthy.common.redis.RedisService;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.diet.domain.dto.DietDetailDto;
import com.tobe.healthy.diet.domain.dto.DietDto;
import com.tobe.healthy.diet.domain.dto.DietFileDto;
import com.tobe.healthy.diet.domain.dto.in.DietAddCommandAtHome;
import com.tobe.healthy.diet.domain.dto.in.DietUpdateCommand;
import com.tobe.healthy.diet.domain.entity.*;
import com.tobe.healthy.diet.repository.DietFileRepository;
import com.tobe.healthy.diet.repository.DietLikeRepository;
import com.tobe.healthy.diet.repository.DietRepository;
import com.tobe.healthy.workout.application.FileService;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import com.tobe.healthy.workout.domain.entity.workoutHistory.WorkoutHistoryLikePK;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    public DietDto addDietAtHome(Member member, DietAddCommandAtHome command) {
        Long memberId = member.getId();
        DietType requestType = command.getType();
        String requestFileUrl = command.getFile();

        Diet diet = dietRepository.getTodayDiet(memberId);

        TrainerMemberMapping mapping = mappingRepository.findTop1ByMemberIdOrderByCreatedAtDesc(memberId).orElse(null);
        Member trainer = mapping == null ? null : mapping.getTrainer();
        if (ObjectUtils.isEmpty(diet)) diet = dietRepository.save(Diet.create(member, trainer));

        diet.deleteFile(requestType);
        if (!ObjectUtils.isEmpty(diet.getDietFiles())) {
            diet.getDietFiles().stream()
                    .filter(f -> requestType.equals(f.getType()))
                    .forEach(file -> fileService.deleteDietFile(getFileName(file.getFileUrl())));
        }

        if (!command.isFast() && !ObjectUtils.isEmpty(requestFileUrl)){
            dietFileRepository.save(DietFiles.create(diet, requestFileUrl, command.getType()));
            redisService.deleteValues(TEMP_FILE_URI.getDescription() + requestFileUrl);
        }

        diet.changeEatDate(command.getEatDate());
        diet.changeFast(command.getType(), command.isFast());
        DietDto dietDto = DietDto.from(diet);
        setDietFile(dietDto, List.of(diet.getDietId()));
        if(isClean(dietDto)) diet.deleteDiet();
        return dietDto;
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

    public DietDto addDiet(Member member, DietUpdateCommand command) {
        TrainerMemberMapping mapping = mappingRepository.findTop1ByMemberIdOrderByCreatedAtDesc(member.getId()).orElse(null);
        Member trainer = mapping == null ? null : mapping.getTrainer();
        Diet diet = dietRepository.save(Diet.create(member, trainer, command));
        uploadDietFiles(diet, command);
        DietDto dietDto = DietDto.from(diet);
        setDietFile(dietDto, List.of(diet.getDietId()));
        return dietDto;
    }

    public DietDto updateDiet(Member member, Long dietId, DietUpdateCommand command) {
        Diet diet = dietRepository.findByDietIdAndMemberIdAndDelYnFalse(dietId, member.getId())
                .orElseThrow(() -> new CustomException(DIET_NOT_FOUND));

        diet.changeEatDate(command.getEatDate());
        diet.changeFast(command);
        deleteOldFiles(diet, command);
        uploadDietFiles(diet, command);

        DietDto dietDto = DietDto.from(diet);
        setDietFile(dietDto, List.of(diet.getDietId()));
        return dietDto;
    }

    private void uploadDietFiles(Diet diet, DietUpdateCommand command) {
        //아침 파일
        if (!command.isBreakfastFast() && !ObjectUtils.isEmpty(command.getBreakfastFile())){
            dietFileRepository.save(DietFiles.create(diet, command.getBreakfastFile(), BREAKFAST));
            redisService.deleteValues(TEMP_FILE_URI.getDescription() + command.getBreakfastFile());
        }else{
            List<DietFiles> files = diet.getDietFiles().stream().filter(f -> BREAKFAST.equals(f.getType())).toList();
            if(!ObjectUtils.isEmpty(files)) fileService.deleteDietFile(getFileName(files.get(0).getFileUrl()));
        }

        //점심 파일
        if (!command.isLunchFast() && !ObjectUtils.isEmpty(command.getLunchFile())){
            dietFileRepository.save(DietFiles.create(diet, command.getLunchFile(), LUNCH));
            redisService.deleteValues(TEMP_FILE_URI.getDescription() + command.getLunchFile());
        }else{
            List<DietFiles> files = diet.getDietFiles().stream().filter(f -> LUNCH.equals(f.getType())).toList();
            if(!ObjectUtils.isEmpty(files)) fileService.deleteDietFile(getFileName(files.get(0).getFileUrl()));
        }

        //저녁 파일
        if (!command.isDinnerFast() && !ObjectUtils.isEmpty(command.getDinnerFile())){
            dietFileRepository.save(DietFiles.create(diet, command.getDinnerFile(), DINNER));
            redisService.deleteValues(TEMP_FILE_URI.getDescription() + command.getDinnerFile());
        }else{
            List<DietFiles> files = diet.getDietFiles().stream().filter(f -> DINNER.equals(f.getType())).toList();
            if(!ObjectUtils.isEmpty(files)) fileService.deleteDietFile(getFileName(files.get(0).getFileUrl()));
        }
    }

    private void deleteOldFiles(Diet diet, DietUpdateCommand command) {
        Set<String> oldFileUrlSet = diet.getDietFiles().stream()
                .filter(f -> !f.getDelYn())
                .map(DietFiles::getFileUrl).collect(Collectors.toSet());
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

    public List<String> getDietUploadDays(Long memberId, String searchDate) {
        List<String> days = dietRepository.getDietUploadDays(memberId, searchDate);
        return ObjectUtils.isEmpty(days) ? null : days;
    }

}
