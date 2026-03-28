package com.tobe.healthy.lessonhistory.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.lessonhistory.domain.dto.out.CommandUploadFileResult;
import com.tobe.healthy.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class LessonHistoryFiles extends BaseTimeEntity<LessonHistoryFiles, Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "lesson_history_files_id")
    private Long id;

    private String fileUrl;

    private int fileOrder;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "lesson_history_id")
    @ToString.Exclude
    private LessonHistory lessonHistory;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "lesson_history_comment_id")
    @ToString.Exclude
    private LessonHistoryComment lessonHistoryComment;

    public LessonHistoryFiles(String fileUrl, int fileOrder, Member member, LessonHistory lessonHistory) {
        this.fileUrl = fileUrl;
        this.fileOrder = fileOrder;
        this.member = member;
        this.lessonHistory = lessonHistory;
    }

    public LessonHistoryFiles(String fileUrl, int fileOrder, Member member, LessonHistory lessonHistory, LessonHistoryComment lessonHistoryComment) {
        this.fileUrl = fileUrl;
        this.fileOrder = fileOrder;
        this.member = member;
        this.lessonHistory = lessonHistory;
        this.lessonHistoryComment = lessonHistoryComment;
    }

    public void updateFileOrder(int fileOrder) {
        this.fileOrder = fileOrder;
    }

    public static LessonHistoryFiles create(Member member, String fileUrl, int fileOrder) {
        return new LessonHistoryFiles(fileUrl, fileOrder, member, null);
    }

    public static LessonHistoryFiles from(CommandUploadFileResult files, LessonHistory lessonHistory, LessonHistoryComment lessonHistoryComment, Member writer) {
        return new LessonHistoryFiles(files.getFileUrl(), files.getFileOrder(), writer, lessonHistory, lessonHistoryComment);
    }
}
