package com.tobe.healthy.member.domain.entity;

import static com.tobe.healthy.member.domain.entity.AlarmStatus.ENABLED;
import static com.tobe.healthy.member.domain.entity.MemberType.MEMBER;
import static com.tobe.healthy.member.domain.entity.SocialType.NONE;
import static com.tobe.healthy.member.domain.entity.TrainerFeedback.ENABLED_RECORD;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.file.domain.entity.Profile;
import com.tobe.healthy.member.domain.dto.in.MemberJoinCommand;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.entity.StandBySchedule;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Getter
@Builder
@DynamicUpdate
public class Member extends BaseTimeEntity<Member, Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    private String userId;

    @Column(unique = true)
    private String email;

    private String password;

    private String name;

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "profile_id")
    private Profile profileId;

    @Enumerated(STRING)
    @Default
    private AlarmStatus alarmStatus = ENABLED;

    @Enumerated(STRING)
    @Default
    private MemberType memberType = MEMBER;

    @ManyToOne(fetch = LAZY, cascade = PERSIST)
    @JoinColumn(name = "gym_id")
    private Gym gym;

    @OneToMany(fetch = LAZY, mappedBy = "trainer")
    @Default
    private List<Schedule> trainerSchedules = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "applicant")
    @Default
    private List<Schedule> applicantSchedules = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    @Default
    private List<StandBySchedule> standBySchedules = new ArrayList<>();

    @Enumerated(STRING)
    @Default
    private SocialType socialType = NONE;

    @Enumerated(STRING)
    @Default
    private TrainerFeedback trainerFeedback = ENABLED_RECORD;

    @ColumnDefault("false")
    @Default
    private boolean delYn = false;

    public static Member join(MemberJoinCommand request, String password) {
        Member member = new Member();
        member.userId = request.getUserId();
        member.email = request.getEmail();
        member.password = password;
        member.name = request.getName();
        member.alarmStatus = ENABLED;
        member.memberType = request.getMemberType();
        return member;
    }

    public static Member join(String email, String name, Profile profile, MemberType memberType, SocialType socialType) {
        return Member.builder()
                .userId(UUID.randomUUID().toString())
                .email(email)
                .name(name)
                .alarmStatus(ENABLED)
                .profileId(profile)
                .memberType(memberType)
                .socialType(socialType)
                .build();
    }

    public static Member join(String email, String name, Profile profile, SocialType socialType) {
        return Member.builder()
            .userId(UUID.randomUUID().toString())
            .email(email)
            .name(name)
            .alarmStatus(ENABLED)
            .profileId(profile)
            .socialType(socialType)
            .build();
    }

    public void registerProfile(Profile profileId) {
        this.profileId = profileId;
    }

    public void resetPassword(String password) {
        this.password = password;
    }

    public void registerGym(Gym gym) {
        this.gym = gym;
    }

    public void deleteMember() {
        this.delYn = true;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeAlarm(AlarmStatus alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    public void changeTrainerFeedback(TrainerFeedback trainerFeedback) {
        this.trainerFeedback = trainerFeedback;
    }
}
