package com.tobe.healthy.diet.domain.dto;

import com.tobe.healthy.diet.domain.entity.DietComment;
import com.tobe.healthy.member.domain.entity.MemberProfile;
import com.tobe.healthy.workout.domain.dto.CommentMemberDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietCommentDto {

    private Long commentId;
    private CommentMemberDto member;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long parentId;
    private Long orderNum;
    private boolean delYn;

    @Builder.Default
    private List<DietCommentDto> replies = null;



    public static DietCommentDto from(DietComment comment) {
        return DietCommentDto.builder()
                .commentId(comment.getCommentId())
                .member(CommentMemberDto.from(comment.getMember()))
                .content(comment.getDelYn() ? "삭제된 댓글입니다." : comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .parentId(comment.getParentCommentId())
                .orderNum(comment.getOrderNum())
                .delYn(comment.getDelYn())
                .build();
    }

    public static DietCommentDto create(DietComment comment, MemberProfile memberProfile) {
        return DietCommentDto.builder()
                .commentId(comment.getCommentId())
                .member(CommentMemberDto.create(comment.getMember(), memberProfile))
                .content(comment.getDelYn() ? "삭제된 댓글입니다." : comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .parentId(comment.getParentCommentId())
                .orderNum(comment.getOrderNum())
                .delYn(comment.getDelYn())
                .build();
    }
}
