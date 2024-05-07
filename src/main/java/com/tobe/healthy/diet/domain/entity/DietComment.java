package com.tobe.healthy.diet.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.diet.domain.dto.in.DietCommentAddCommand;
import com.tobe.healthy.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "diet_comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class DietComment extends BaseTimeEntity<DietComment, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_id")
    private Diet diet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String content;

    @ColumnDefault("false")
    @Builder.Default
    private Boolean delYn = false;

    private Long parentCommentId;
    private Long depth;
    private Long orderNum;

    public static DietComment create(Diet diet,
                                     Member member,
                                     DietCommentAddCommand command,
                                     Long depth,
                                     Long orderNum) {
        return DietComment.builder()
                .diet(diet)
                .member(member)
                .content(command.getContent())
                .parentCommentId(command.getParentCommentId())
                .depth(depth)
                .orderNum(orderNum)
                .build();
    }

    public void deleteComment() {
        this.delYn = true;
    }

    public void updateContent(String content){
        this.content = content;
    }
}
