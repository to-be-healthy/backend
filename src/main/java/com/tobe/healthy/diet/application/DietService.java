package com.tobe.healthy.diet.application;

import com.tobe.healthy.diet.domain.dto.DietDto;
import com.tobe.healthy.diet.domain.entity.Diet;
import com.tobe.healthy.diet.repository.DietRepository;
import com.tobe.healthy.file.domain.dto.DietFileDto;
import com.tobe.healthy.file.domain.entity.DietFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DietService {

    private final DietRepository dietRepository;

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


}
