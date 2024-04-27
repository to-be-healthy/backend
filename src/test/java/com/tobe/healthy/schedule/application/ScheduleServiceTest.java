package com.tobe.healthy.schedule.application;

import com.tobe.healthy.schedule.domain.dto.in.AutoCreateScheduleCommand;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleInfo;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static java.time.LocalDate.of;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
@Transactional
@DisplayName("일정 기능 테스트")
class ScheduleServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ScheduleService scheduleService;

    @Nested
    @DisplayName("일정 등록")
    class CreateSchedule {

        @Test
        @DisplayName("임시 일정을 생성한다.")
        void autoCreateSchedule() {
            AutoCreateScheduleCommand request = AutoCreateScheduleCommand.builder()
                    .startDt(of(2024, 04, 01))
                    .endDt(of(2024, 04, 07))
                    .weekdayStartTime(LocalTime.of(10, 00))
                    .weekdayEndTime(LocalTime.of(20, 00))
                    .weekendStartTime(LocalTime.of(12, 00))
                    .weekendEndTime(LocalTime.of(18, 00))
                    .lessonTime(50)
                    .breakTime(10)
                    .build();

            TreeMap<LocalDate, ArrayList<ScheduleInfo>> result = scheduleService.autoCreateSchedule(request);

            for (Map.Entry<LocalDate, ArrayList<ScheduleInfo>> m : result.entrySet()) {
                LocalDate key = m.getKey();
                ArrayList<ScheduleInfo> value = result.get(key);
                log.info("value => {}", value);
            }

            assertThat(result.size()).isEqualTo(7);
        }

        @Test
        @DisplayName("일정을 등록한다.")
        void registerSchedule() {
            AutoCreateScheduleCommand request = AutoCreateScheduleCommand.builder()
                    .startDt(of(2024, 04, 01))
                    .endDt(of(2024, 04, 07))
                    .weekdayStartTime(LocalTime.of(10, 00))
                    .weekdayEndTime(LocalTime.of(20, 00))
                    .weekendStartTime(LocalTime.of(12, 00))
                    .weekendEndTime(LocalTime.of(18, 00))
                    .lessonTime(50)
                    .breakTime(10)
                    .build();

            TreeMap<LocalDate, ArrayList<ScheduleInfo>> result = scheduleService.autoCreateSchedule(request);


        }

        /*@Test
        @DisplayName("수업신청 테스트")
        void reserveScheduleForMultiThreadTest() throws InterruptedException {
            AtomicInteger successCount = new AtomicInteger();
            int numberOfExcute = 100;
            ExecutorService service = Executors.newFixedThreadPool(10);
            CountDownLatch latch = new CountDownLatch(numberOfExcute);

            Long memberId = 739L;
            for (int i=0; i<numberOfExcute; i++) {
                service.execute(() -> {
                    try {
                        scheduleService.reserveSchedule(1512L, memberId);
                        successCount.getAndIncrement();
                        System.out.println("성공");
                    } catch (ObjectOptimisticLockingFailureException oe) {
                        System.out.println("충돌감지");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    latch.countDown();
                });
            }
            latch.await();

            assertThat(successCount.get()).isEqualTo(1);
        }*/
    }
}
