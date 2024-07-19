package com.tobe.healthy.member.domain.entity;

import static com.tobe.healthy.member.domain.entity.AlarmStatus.ENABLED;
import static com.tobe.healthy.member.domain.entity.MemberType.STUDENT;
import static com.tobe.healthy.member.domain.entity.MemberType.TRAINER;
import static com.tobe.healthy.member.domain.entity.SocialType.NONE;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.gym.domain.entity.Gym;
import com.tobe.healthy.member.domain.dto.in.CommandJoinMember;
import com.tobe.healthy.push.domain.entity.MemberToken;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.entity.ScheduleWaiting;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = PROTECTED)
@Getter
@DynamicUpdate
@ToString
public class Member extends BaseTimeEntity<Member, Long> {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "member_id")
	private Long id;

	private String userId;

	private String email;

	@ToString.Exclude
	private String password;

	private String name;

	@ManyToOne(fetch = LAZY, cascade = ALL)
	@JoinColumn(name = "member_profile_id")
	@Nullable
	@ToString.Exclude
	private MemberProfile memberProfile;

	@Enumerated(STRING)
	@ColumnDefault("'STUDENT'")
	@Builder.Default
	private MemberType memberType = STUDENT;

	@Enumerated(STRING)
	@ColumnDefault("'ENABLED'")
	@Builder.Default
	private AlarmStatus pushAlarmStatus = ENABLED;

	@Enumerated(STRING)
	@ColumnDefault("'ENABLED'")
	@Builder.Default
	private AlarmStatus communityAlarmStatus = ENABLED;

	@Enumerated(STRING)
	@ColumnDefault("'ENABLED'")
	@Builder.Default
	private AlarmStatus feedbackAlarmStatus = ENABLED;

	@Enumerated(STRING)
	@ColumnDefault("'ENABLED'")
	@Builder.Default
	private AlarmStatus scheduleNoticeStatus = ENABLED;

	@Enumerated(STRING)
	@ColumnDefault("'ENABLED'")
	@Builder.Default
	private AlarmStatus dietNoticeStatus = ENABLED;

	@ManyToOne(fetch = LAZY, cascade = PERSIST)
	@JoinColumn(name = "gym_id")
	@Nullable
	@ToString.Exclude
	private Gym gym;

	@OneToMany(fetch = LAZY, mappedBy = "member")
	@Builder.Default
	@ToString.Exclude
	private final List<MemberToken> memberToken = new ArrayList<>();

	@OneToMany(fetch = LAZY, mappedBy = "trainer")
	@Builder.Default
	@ToString.Exclude
	private final List<Schedule> trainerSchedules = new ArrayList<>();

	@OneToMany(fetch = LAZY, mappedBy = "applicant")
	@Builder.Default
	@ToString.Exclude
	private final List<Schedule> applicantSchedules = new ArrayList<>();

	@OneToMany(fetch = LAZY, mappedBy = "member")
	@Builder.Default
	@ToString.Exclude
	private final List<ScheduleWaiting> scheduleWaitings = new ArrayList<>();

	@Enumerated(STRING)
	@ColumnDefault("'NONE'")
	@Builder.Default
	private SocialType socialType = NONE;

	private String nickname;

	@ColumnDefault("false")
	@Builder.Default
	private boolean delYn = false;

	private String invitationLink;

	public static Member join(CommandJoinMember request, String password) {
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

	public static Member join(String email, String name, MemberType memberType, SocialType socialType) {
		return Member.builder()
				.userId(UUID.randomUUID().toString())
				.email(email)
				.name(name)
				.pushAlarmStatus(ENABLED)
				.memberType(memberType)
				.socialType(socialType)
				.build();
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

	public void changeAlarm(AlarmType type, AlarmStatus alarmStatus) {
		switch (type) {
			case PUSH -> this.pushAlarmStatus = alarmStatus;
			case COMMUNITY -> this.communityAlarmStatus = alarmStatus;
			case FEEDBACK -> this.feedbackAlarmStatus = alarmStatus;
			case SCHEDULENOTICE -> this.scheduleNoticeStatus = alarmStatus;
		}
	}

	public void changeTrainerFeedback(AlarmStatus alarmStatus) {
		this.feedbackAlarmStatus = alarmStatus;
	}

	public void changeScheduleNotice(AlarmStatus alarmStatus) {
		this.scheduleNoticeStatus = alarmStatus;
	}

	public void changeDietNotice(AlarmStatus alarmStatus) {
		this.dietNoticeStatus = alarmStatus;
	}

	public void assignNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setMemberProfile(MemberProfile memberProfile) {
		this.memberProfile = memberProfile;
	}

	public void changeEmail(String email) {
		this.email = email;
	}

	public void registerProfile(String fileName, String fileUrl) {
		this.memberProfile = MemberProfile.create(fileName, fileUrl, this);
	}

	public String getTransformedMemberType() {
		return switch (this.memberType) {
			case TRAINER -> TRAINER.getDescription();
			case STUDENT -> "회원으";
		};
	}

	public void deleteProfile() {
		this.memberProfile = null;
	}

	public void updateNonMemberInfo(CommandJoinMember request, String password) {
		this.userId = request.getUserId();
		this.email = request.getEmail();
		this.password = password;
		this.name = request.getName();
		this.pushAlarmStatus = ENABLED;
		this.memberType = request.getMemberType();
		this.socialType = NONE;
	}
}
