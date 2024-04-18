package com.tobe.healthy.point.repository;


import com.tobe.healthy.point.domain.entity.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PointRepositoryCustom {

    Page<Point> getPoint(Long memberId, String searchDate, Pageable pageable);
}
