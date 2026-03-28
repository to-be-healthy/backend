package com.tobe.healthy.point.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tobe.healthy.point.domain.entity.Point;

public interface PointRepositoryCustom {

	Page<Point> getPoint(Long memberId, String searchDate, Pageable pageable);
}
