package com.tobe.healthy.diet.repository;


public interface DietLikeRepositoryCustom {

    Long getLikeCnt(Long dietId);
    void deleteLikeByDietId(Long dietId);

}
