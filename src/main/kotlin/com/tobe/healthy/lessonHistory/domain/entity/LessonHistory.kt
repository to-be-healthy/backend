package com.tobe.healthy.lessonHistory.domain.entity

import com.tobe.healthy.common.BaseTimeEntity
import com.tobe.healthy.file.domain.entity.AwsS3File
import com.tobe.healthy.lessonHistory.domain.dto.`in`.RegisterLessonHistoryCommand
import com.tobe.healthy.lessonHistory.domain.entity.FeedbackCheckStatus.UNREAD
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.schedule.domain.entity.Schedule
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicUpdate
class LessonHistory(

    var title: String,

    var content: String,

    @OneToMany(fetch = LAZY, mappedBy = "lessonHistory", cascade = [ALL])
    val lessonHistoryComment: MutableList<LessonHistoryComment> = mutableListOf(),

    @OneToMany(fetch = LAZY, mappedBy = "lessonHistory", cascade = [ALL])
    var file: MutableList<AwsS3File> = mutableListOf(),

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "trainer_id")
    val trainer: Member,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "student_id")
    val student: Member,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "schedule_id")
    val schedule: Schedule,

    @Enumerated(STRING)
    var feedbackChecked: FeedbackCheckStatus = UNREAD,

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "lesson_history_id")
    val id: Long = 0
) : BaseTimeEntity<LessonHistory, Long>() {

    fun updateLessonHistory(title: String, content: String) {
        this.title = title
        this.content = content
    }

    fun updateFeedbackStatus(feedbackStatus: FeedbackCheckStatus) {
        this.feedbackChecked = feedbackStatus
    }

    companion object {
        fun register(request: RegisterLessonHistoryCommand, student: Member, trainer: Member, schedule: Schedule): LessonHistory {
            return LessonHistory(
                title = request.title!!,
                content = request.content!!,
                trainer = trainer,
                student = student,
                schedule = schedule
            )
        }
    }
}
