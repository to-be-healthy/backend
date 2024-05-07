package com.tobe.healthy.point.domain.entity;

import static jakarta.persistence.EnumType.STRING;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.member.domain.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;


@Entity
@Table(name = "point")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Point extends BaseTimeEntity<Point, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Long pointId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ColumnDefault("0")
    private int point;

    @Enumerated(STRING)
    @ColumnDefault("'PLUS'")
    private Calculation calculation;

    @Enumerated(STRING)
    @ColumnDefault("'WORKOUT'")
    private PointType type;

    public static Point create(Member member, PointType type, Calculation calculation, int point) {
        return Point.builder()
                .member(member)
                .type(type)
                .calculation(calculation)
                .point(point)
                .build();
    }

}
