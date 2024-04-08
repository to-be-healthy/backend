package com.tobe.healthy.point.repository;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberType;
import com.tobe.healthy.point.domain.entity.Calculation;
import com.tobe.healthy.point.domain.entity.Point;
import com.tobe.healthy.point.domain.entity.PointType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface PointRepository extends JpaRepository<Point, Long> {

    @Query(value = "select RANK() OVER (ORDER BY a.point_sum desc) AS ranking, a.member_id, a.point_sum " +
            "from (" +
                "select p.member_id, " +
                "sum(case when p.calculation = 'PLUS' then p.point " +
                "when p.calculation = 'MINUS' then p.point*(-1) end) AS point_sum " +
                "from point p " +
                "where p.created_at between date_format(now() - interval 1 month, '%Y-%m-01 00:00:00') " +
                "and last_day(now() - interval 1 month) + interval 1 day - interval 1 second " +
                "group by p.member_id " +
                "having p.member_id in(:members)) a", nativeQuery = true)
    List<Object[]> calculateRank(List<Long> members);

//    @Query("select count(p.point_id) from point p where p.member_id = :memberId and p.type = :type " +
//            " and p.calculation = :calculation and DATE_FORMAT(p.created_at , '%Y-%m-%d') = CURDATE()")
    long countByMemberIdAndTypeAndCalculationAndCreatedAtBetween(Long memberId, PointType type, Calculation calculation, LocalDateTime start, LocalDateTime end);

}
