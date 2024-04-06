package com.tobe.healthy.point.repository;

import com.tobe.healthy.point.domain.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


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

}
