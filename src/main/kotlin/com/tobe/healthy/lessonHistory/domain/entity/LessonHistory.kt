package com.tobe.healthy.lessonHistory.domain.entity

import com.tobe.healthy.common.BaseTimeEntity
import com.tobe.healthy.file.domain.entity.Profile
import com.tobe.healthy.lessonHistory.domain.dto.RegisterLessonHistoryCommand
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.schedule.domain.entity.Schedule
import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicUpdate
class LessonHistory(

    var title: String,

    var content: String,

    @OneToMany(fetch = LAZY, mappedBy = "lessonHistory", cascade = [CascadeType.ALL])
    val lessonHistoryComment: MutableList<LessonHistoryComment>? = null,

    @OneToMany(fetch = LAZY, mappedBy = "lessonHistory")
    var file: MutableList<Profile>? = null,

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
) : BaseTimeEntity<LessonHistory, Long>() {

    fun updateLessonHistory(title: String, content: String) {
        this.title = title
        this.content = content
    }

    companion object {

        fun register(request: RegisterLessonHistoryCommand, student: Member, trainer: Member, schedule: Schedule): LessonHistory {
            return LessonHistory(
                title = request.title,
                content = request.content,
                trainer = trainer,
                student = student,
                schedule = schedule
            )
        }
    }
}