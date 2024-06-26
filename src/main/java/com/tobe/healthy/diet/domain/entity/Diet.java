package com.tobe.healthy.diet.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.diet.domain.dto.in.DietAddCommand;
import com.tobe.healthy.diet.domain.dto.in.DietUpdateCommand;
import com.tobe.healthy.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "diet")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@DynamicUpdate
@ToString
public class Diet extends BaseTimeEntity<Diet, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diet_id")
    private Long dietId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "trainer_id")
    @ToString.Exclude
    private Member trainer;

    @ColumnDefault("false")
    @Builder.Default
    private Boolean delYn = false;

    @ColumnDefault("0")
    @Builder.Default
    private Long likeCnt = 0L;

    @ColumnDefault("0")
    @Builder.Default
    private Long commentCnt = 0L;

    @ColumnDefault("false")
    @Builder.Default
    private Boolean fastBreakfast = false;

    @ColumnDefault("false")
    @Builder.Default
    private Boolean fastLunch = false;

    @ColumnDefault("false")
    @Builder.Default
    private Boolean fastDinner = false;

    private LocalDate eatDate;

    @OneToMany(fetch = LAZY, mappedBy = "diet", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    private List<DietFiles> dietFiles = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = LAZY, mappedBy = "diet", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<DietComment> dietComments = new ArrayList<>();

    public List<DietFiles> getDietFiles() {
        return dietFiles.stream().filter(f -> !f.getDelYn()).toList();
    }

    public static Diet create(Member member, Member trainer){
        return Diet.builder()
                .member(member)
                .trainer(trainer)
                .build();
    }

    public static Diet create(Member member, Member trainer, DietAddCommand command) {
        return Diet.builder()
                .member(member)
                .trainer(trainer)
                .fastBreakfast(command.isBreakfastFast())
                .fastLunch(command.isLunchFast())
                .fastDinner(command.isDinnerFast())
                .eatDate(LocalDate.parse(command.getEatDate(), DateTimeFormatter.ISO_DATE))
                .build();
    }

    public void updateLikeCnt(Long likeCnt){
        this.likeCnt = likeCnt;
    }

    public void changeFast(DietType type, boolean isFast){
        switch (type) {
            case BREAKFAST -> this.changeFastBreakfast(isFast);
            case LUNCH -> this.changeFastLunch(isFast);
            case DINNER -> this.changeFastDinner(isFast);
        }
    }

    public void changeFast(DietUpdateCommand command) {
        this.changeFastBreakfast(command.isBreakfastFast());
        this.changeFastLunch(command.isLunchFast());
        this.changeFastDinner(command.isDinnerFast());
    }

    public void changeFastBreakfast(boolean isFast){
        this.fastBreakfast = isFast;
    }

    public void changeFastLunch(boolean isFast){
        this.fastLunch = isFast;
    }

    public void changeFastDinner(boolean isFast){
        this.fastDinner = isFast;
    }

    public void deleteFile(DietType type) {
        if(dietFiles != null){
            this.dietFiles.stream().filter(f -> type.equals(f.getType()))
                    .forEach(DietFiles::deleteDietFile);
        }
    }

    public void deleteDiet() {
        this.delYn = true;
        this.deleteFiles();
        this.deleteComments();
    }

    public void deleteComments() {
        this.dietComments.forEach(DietComment::deleteComment);
    }

    public void deleteFiles() {
        this.dietFiles.forEach(DietFiles::deleteDietFile);
    }

    public void updateCommentCnt(Long commentCnt) {
        this.commentCnt = commentCnt;
    }

    public void changeEatDate(String eatDate) {
        this.eatDate = LocalDate.parse(eatDate, DateTimeFormatter.ISO_DATE);
    }

    public void changeTrainer(Member trainer) {
        this.trainer = trainer;
    }
}
