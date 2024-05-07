package com.tobe.healthy.applicationform.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "application_form")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Getter
public class ApplicationForm extends BaseTimeEntity<ApplicationForm, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_form_id")
    private Long applicationFormId;

    private Long scheduleId; //Schedule schedule
    private Long memberId;
    @ColumnDefault("'N'")
    private String completed;

}

