package com.tobe.healthy.common;

import com.tobe.healthy.point.application.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final PointService pointService;

    //매월 1일 오전 1시
    @Scheduled(cron = "0 0 1 1 * *")
    public void updateMemberRankScheduler(){
        log.info("========== 트레이너의 학생들 랭킹 산정 스케줄러 작동 시작 ==========");
        pointService.updateMemberRank();
        log.info("========== 트레이너의 학생들 랭킹 산정 스케줄러 작동 완료 ==========");
    }

}
