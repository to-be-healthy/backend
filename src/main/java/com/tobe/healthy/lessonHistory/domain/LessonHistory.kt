package com.tobe.healthy.lessonHistory.domain

import com.tobe.healthy.common.BaseTimeEntity
import com.tobe.healthy.file.domain.entity.Profile
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.schedule.domain.entity.Schedule
import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY

@Entity
class LessonHistory(

    val title: String,

    val content: String,

    @OneToMany(fetch = LAZY, mappedBy = "lessonHistory")
    val file: MutableList<Profile>? = null,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "trainer_id")
    val trainer: Member,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "student_id")
    val student: Member,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "schedule_id")
    val schedule: Schedule,

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "lesson_history_id")
    val id: Long? = null
) : BaseTimeEntity<LessonHistory, Long>()