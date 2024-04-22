package com.tobe.healthy.diet.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.diet.domain.dto.DietDto;
import com.tobe.healthy.diet.domain.entity.Diet;
import com.tobe.healthy.diet.domain.entity.DietLike;
import com.tobe.healthy.diet.domain.entity.DietLikePK;
import com.tobe.healthy.diet.repository.DietLikeRepository;
import com.tobe.healthy.diet.repository.DietRepository;
import com.tobe.healthy.file.domain.dto.DietFileDto;
import com.tobe.healthy.file.domain.entity.DietFile;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.domain.entity.WorkoutHistory;
import com.tobe.healthy.workout.domain.entity.WorkoutHistoryLike;
import com.tobe.healthy.workout.domain.entity.WorkoutHistoryLikePK;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.tobe.healthy.config.error.ErrorCode.*;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DietService {

    private final DietRepository dietRepository;
    private final DietLikeRepository dietLikeRepository;

    public List<DietDto> getDiet(Long memberId, Pageable pageable, String searchDate) {
        Page<Diet> diets = dietRepository.getDietOfMonth(memberId, pageable, searchDate);
        List<DietDto> dietDtos = diets.map(DietDto::from).stream().toList();
        List<Long> ids = dietDtos.stream().map(DietDto::getDietId).collect(Collectors.toList());
        dietDtos = setDietFile(dietDtos, ids);
        return dietDtos.isEmpty() ? null : dietDtos;
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
}
