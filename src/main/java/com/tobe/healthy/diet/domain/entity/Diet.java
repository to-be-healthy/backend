package com.tobe.healthy.diet.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.diet.domain.dto.in.DietAddCommand;
import com.tobe.healthy.file.domain.entity.DietFile;
import com.tobe.healthy.file.domain.entity.DietType;
import com.tobe.healthy.file.domain.entity.WorkoutHistoryFile;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.domain.entity.WorkoutHistoryComment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.tobe.healthy.file.domain.entity.DietType.BREAKFAST;

@Entity
@Table(name = "diet")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@DynamicUpdate
public class Diet extends BaseTimeEntity<Diet, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diet_id")
    private Long dietId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
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

    @OneToMany(mappedBy = "diet", cascade = CascadeType.ALL)
    @Builder.Default
    private List<DietFile> dietFiles = new ArrayList<>();

//    @Builder.Default
//    @OneToMany(mappedBy = "diet", cascade = CascadeType.ALL)
//    private List<DietComment> dietComments = new ArrayList<>();


    public static Diet create(Member member, Member trainer){
        return Diet.builder()
                .member(member)
                .trainer(trainer)
                .build();
    }

    public void updateLikeCnt(Long likeCnt){
        this.likeCnt = likeCnt;
    }

    public void changeFast(DietAddCommand command){
        switch (command.getType()) {
            case BREAKFAST -> fastBreakfast = command.isFast();
            case LUNCH -> fastLunch = command.isFast();
            case DINNER -> fastDinner = command.isFast();
        }
    }

    public void deleteFile(DietType type) {
        if(dietFiles != null){
            this.dietFiles.stream().filter(f -> type.equals(f.getType()))
                    .forEach(DietFile::deleteDietFile);
        }
    }

    public void deleteDiet() {
        this.delYn = true;
        this.deleteFiles();
//        this.deleteComments();
    }

//    public void deleteComments() {
//        this.dietComments.forEach(WorkoutHistoryComment::deleteComment);
//    }

    public void deleteFiles() {
        this.dietFiles.forEach(DietFile::deleteDietFile);
    }

}
