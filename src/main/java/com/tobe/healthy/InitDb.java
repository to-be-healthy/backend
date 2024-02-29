package com.tobe.healthy;

import static com.tobe.healthy.member.domain.entity.Alarm.ABLE;
import static com.tobe.healthy.member.domain.entity.MemberCategory.MEMBER;
import static com.tobe.healthy.member.domain.entity.MemberCategory.TRAINER;
import static com.tobe.healthy.schedule.domain.entity.ReserveType.TRUE;
import static java.time.LocalDateTime.of;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.entity.StandBySchedule;
import com.tobe.healthy.schedule.repository.ScheduleRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("DEV")
public class InitDb {

    private final InitService initService;
    @PostConstruct
    public void init() {
        initService.dbInit1();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final ScheduleRepository scheduleRepository;
        private final EntityManager em;
        private static final LocalDateTime localDateTime = of(2024, 2, 29, 9, 0);

        public void dbInit1() {
            log.info("Init1 = {}", this.getClass());
            Member trainer = Member.builder()
                .email("laborlawseon2@gmail.com")
                .password("123456789")
                .nickname("seonwoo_jung2")
                .isAlarm(ABLE)
                .category(TRAINER)
                .mobileNum("010-4321-4321")
                .build();

            // given
            for (int i = 1; i <= 10; i++) {
                localDateTime.plusHours(1);
                Schedule schedule = Schedule.builder()
                    .startDate(localDateTime.plusHours(i))
                    .isReserve(TRUE)
                    .round(String.valueOf(i))
                    .trainerId(trainer)
                    .build();

                em.persist(schedule);
            }

            Member member = Member.builder()
                .email("laborlawseon@gmail.com")
                .password("123456789")
                .nickname("seonwoo_jung")
                .isAlarm(ABLE)
                .category(MEMBER)
                .mobileNum("010-4321-4321")
                .build();

            Optional<Schedule> entity = scheduleRepository.findByStartDate(of(2024, 2, 29, 10, 0));
            if (entity.isPresent()) {
                StandBySchedule standBySchedule = StandBySchedule.builder().schedule(entity.get())
                    .member(member).build();
                em.persist(standBySchedule);
            }
        }
    }
}