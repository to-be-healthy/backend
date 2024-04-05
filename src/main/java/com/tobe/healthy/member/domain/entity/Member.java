package com.tobe.healthy.member.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.file.domain.entity.Profile;
import com.tobe.healthy.gym.domain.entity.Gym;
import com.tobe.healthy.member.domain.dto.in.MemberJoinCommand;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.entity.StandBySchedule;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.tobe.healthy.member.domain.entity.AlarmStatus.ENABLED;
import static com.tobe.healthy.member.domain.entity.MemberType.STUDENT;
import static com.tobe.healthy.member.domain.entity.SocialType.NONE;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@DynamicUpdate
public class Member extends BaseTimeEntity<Member, Long> {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "member_id")
	private Long id;

	private String userId;

	private String email;

	private String password;

	private String name;

	@ColumnDefault("0")
	private int age = 0;

	@ColumnDefault("0")
	private int height = 0;

	@ColumnDefault("0")
	private int weight = 0;

	@OneToOne(fetch = LAZY, cascade = ALL)
	@JoinColumn(name = "profile_id")
	private Profile profileId;

	@Enumerated(STRING)
	@ColumnDefault("'STUDENT'")
	private MemberType memberType = STUDENT;

	@Enumerated(STRING)
	@ColumnDefault("'ENABLED'")
	private AlarmStatus pushAlarmStatus = ENABLED;

	@Enumerated(STRING)
	@ColumnDefault("'ENABLED'")
	private AlarmStatus feedbackAlarmStatus = ENABLED;

	@ManyToOne(fetch = LAZY, cascade = PERSIST)
	@JoinColumn(name = "gym_id")
	private Gym gym;

	@OneToMany(fetch = LAZY, mappedBy = "trainer")
	private final List<Schedule> trainerSchedules = new ArrayList<>();

	@OneToMany(fetch = LAZY, mappedBy = "applicant")
	private final List<Schedule> applicantSchedules = new ArrayList<>();

	@OneToMany(mappedBy = "member")
	private final List<StandBySchedule> standBySchedules = new ArrayList<>();

	@Enumerated(STRING)
	@ColumnDefault("'NONE'")
	private SocialType socialType = NONE;

	private String nickname;

	@ColumnDefault("false")
	private boolean delYn = false;

	public static Member join(MemberJoinCommand request, String password) {
		return Member.builder()
				.userId(request.getUserId())
				.email(request.getEmail())
				.password(password)
				.name(request.getName())
				.pushAlarmStatus(ENABLED)
				.memberType(request.getMemberType())
				.socialType(NONE)
				.build();
	}

	public static Member join(String email, String name, Profile profile, MemberType memberType, SocialType socialType) {
		return Member.builder()
				.userId(UUID.randomUUID().toString())
				.email(email)
				.name(name)
				.pushAlarmStatus(ENABLED)
				.profileId(profile)
				.memberType(memberType)
				.socialType(socialType)
				.build();
	}

	@Builder
	public Member(String userId, String email, String password, String name, Profile profileId, MemberType memberType, AlarmStatus pushAlarmStatus, AlarmStatus feedbackAlarmStatus, Gym gym, SocialType socialType, boolean delYn) {
		this.userId = userId;
		this.email = email;
		this.password = password;
		this.name = name;
		this.profileId = profileId;
		this.memberType = memberType;
		this.pushAlarmStatus = pushAlarmStatus;
		this.feedbackAlarmStatus = feedbackAlarmStatus;
		this.gym = gym;
		this.socialType = socialType;
		this.delYn = delYn;
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
		this.pushAlarmStatus = alarmStatus;
	}

	public void changeTrainerFeedback(AlarmStatus alarmStatus) {
		this.feedbackAlarmStatus = alarmStatus;
	}

	public void changeAge(int age){
		this.age = age;
	}

	public void changeHeight(int height){
		this.height = height;
	}

	public void changeWeight(int weight){
		this.weight = weight;
	}
}
