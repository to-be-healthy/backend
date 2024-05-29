package com.tobe.healthy.diet.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.diet.domain.dto.DietCommentDto;
import com.tobe.healthy.diet.domain.dto.in.DietCommentAddCommand;
import com.tobe.healthy.diet.domain.entity.Diet;
import com.tobe.healthy.diet.domain.entity.DietComment;
import com.tobe.healthy.diet.repository.DietCommentRepository;
import com.tobe.healthy.diet.repository.DietRepository;
import com.tobe.healthy.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tobe.healthy.config.error.ErrorCode.COMMENT_NOT_FOUND;
import static com.tobe.healthy.config.error.ErrorCode.DIET_NOT_FOUND;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DietCommentService {

    private final DietCommentRepository commentRepository;
    private final DietRepository dietRepository;


    public List<DietCommentDto> getCommentsByDietId(Long dietId, Pageable pageable) {
        List<DietComment> comments = commentRepository.getCommentsByDietId(dietId, pageable).stream().toList();
        return settingReplyFormat(comments);
    }

    private List<DietCommentDto> settingReplyFormat(List<DietComment> comments) {
        List<DietCommentDto> dtos = comments.stream()
                .map(c -> DietCommentDto.create(c, c.getMember().getMemberProfile())).toList();
        Map<Boolean, List<DietCommentDto>> dtos2 = dtos.stream()
                .collect(Collectors.partitioningBy(c -> c.getParentId() == null));
        List<DietCommentDto> parent = dtos2.get(true);
        List<DietCommentDto> child = dtos2.get(false);

        Map<Long, List<DietCommentDto>> childByGroupList = child.stream()
                .collect(Collectors.groupingBy(DietCommentDto::getParentId, Collectors.toList()));
        return parent.stream().peek(p -> p.setReplies(childByGroupList.get(p.getId()))).toList();
    }

    public void addComment(Long dietId, DietCommentAddCommand command, Member member) {
        Diet diet = dietRepository.findById(dietId)
                .orElseThrow(() -> new CustomException(DIET_NOT_FOUND));

        boolean isReply = command.getParentCommentId() != null;
        Long depth, orderNum;
        Long commentCnt = commentRepository.countByDiet(diet);
        if(isReply){
            DietComment parentComment = commentRepository.findByCommentIdAndDelYnFalse(command.getParentCommentId())
                    .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
            depth = parentComment.getDepth()+1;
            orderNum = parentComment.getOrderNum();
        }else{
            depth = 0L;
            orderNum = commentCnt;
        }
        commentRepository.save(DietComment.create(diet, member, command, depth, orderNum));
        diet.updateCommentCnt(++commentCnt);
    }

    public DietCommentDto updateComment(Member member, Long dietId, Long commentId, DietCommentAddCommand command) {
        DietComment comment = commentRepository.findByCommentIdAndMemberIdAndDelYnFalse(commentId, member.getId())
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
        comment.updateContent(command.getContent());
        return DietCommentDto.from(comment);
    }

    public void deleteComment(Member member, Long dietId, Long commentId) {
        DietComment comment = commentRepository.findByCommentIdAndMemberIdAndDelYnFalse(commentId, member.getId())
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
        comment.deleteComment();
    }
}
