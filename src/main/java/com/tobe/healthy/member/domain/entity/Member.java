package com.tobe.healthy.member.domain.entity;

import static com.tobe.healthy.member.domain.entity.AlarmStatus.ENABLED;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.file.domain.entity.Profile;
import com.tobe.healthy.member.domain.dto.in.MemberJoinCommand;
import com.tobe.healthy.schedule.domain.entity.Schedule;
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
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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
    private String userId;
    private String email;
    private String password;
    private String name;

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "profile_id")
    private Profile profileId;

    @Enumerated(STRING)
    private AlarmStatus alarmStatus;

    @Enumerated(STRING)
    private MemberType memberType;

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "gym_id")
    private Gym gym;

    @OneToMany(fetch = LAZY, mappedBy = "trainer")
    @Default
    private List<Schedule> trainerSchedules = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "applicant")
    @Default
    private List<Schedule> applicantSchedules = new ArrayList<>();

    @ColumnDefault("'N'")
    @Default
    private char delYn = 'N';

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

    public void registerProfile(Profile profileId) {
        this.profileId = profileId;
    }

    public void resetPassword(String password) {
        this.password = password;
    }

    public void deleteMember() {
        this.delYn = 'Y';
    }
}
