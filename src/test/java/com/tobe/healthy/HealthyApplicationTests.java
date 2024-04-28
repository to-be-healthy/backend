package com.tobe.healthy;

import com.tobe.healthy.course.application.CourseService;
import com.tobe.healthy.course.domain.dto.in.CourseUpdateCommand;
import com.tobe.healthy.course.domain.entity.CourseHistoryType;
import com.tobe.healthy.point.domain.entity.Calculation;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Slf4j
@Transactional
@Rollback(false)
class HealthyApplicationTests {

	@Autowired
	private CourseService courseService;

//	학생0 memberId: 541
//	트레이너 memberId: 542
//	schedule_id: 438 ~ 565

	@Test
	void contextLoads() {
		for (int i = 0; i < 100; i++) {
			courseService.updateCourse(542L, 39L, new CourseUpdateCommand(541L, Calculation.PLUS, CourseHistoryType.PLUS_CNT, 5));
		}
	}
}
