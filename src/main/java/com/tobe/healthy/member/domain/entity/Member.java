package com.tobe.healthy.member.domain.entity;

import static com.tobe.healthy.member.domain.entity.Alarm.ABLE;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.file.domain.entity.Profile;
import com.tobe.healthy.member.domain.dto.in.MemberRegisterCommand;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.entity.StandBySchedule;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Getter
@Builder
public class Member extends BaseTimeEntity<Member, Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String email;

    private String password;

    private String nickname;

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "profile_id")
    private Profile profileId;

    @Enumerated(STRING)
    private Alarm isAlarm;

    @Enumerated(STRING)
    private MemberCategory category;

    private String mobileNum;

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "gym_id")
    private Gym gym;

    @OneToMany(mappedBy = "trainerId")
    private List<Schedule> trainerSchedules = new ArrayList<>();

    @OneToMany(mappedBy = "applicantId")
    private List<Schedule> applicantSchedules = new ArrayList<>();

    @OneToOne(mappedBy = "member")
    private StandBySchedule standBySchedule;

    public static Member create(MemberRegisterCommand request, String password) {
        Member member = new Member();
        member.email = request.getEmail();
        member.password = password;
        member.nickname = request.getNickname();
        member.isAlarm = ABLE;
        member.category = request.getCategory();
        member.mobileNum = request.getMobileNum();
        return member;
    }

    public void registerProfile(Profile profileId) {
        this.profileId = profileId;
    }
}
