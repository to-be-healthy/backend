package com.tobe.healthy.member.domain.dto.out;

import com.tobe.healthy.gym.domain.dto.out.GymDto;
import com.tobe.healthy.member.domain.dto.ProfileDto;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
public class RetrieveTrainerInfo {

    private Long mappingId;
    private TrainerInfo trainer;

    public static RetrieveTrainerInfo from(TrainerMemberMapping trainerMemberMapping) {
        return RetrieveTrainerInfo.builder()
                .mappingId(trainerMemberMapping.getMappingId())
                .trainer(TrainerInfo.from(trainerMemberMapping.getTrainer()))
                .build();
    }

    @Builder
    @AllArgsConstructor
    @Data
    static class TrainerInfo {
        private Long id;
        private String email;
        private String name;
        private ProfileDto profile;
        private GymDto gym;

        public static TrainerInfo from(Member trainer) {
            return TrainerInfo.builder()
                    .id(trainer.getId())
                    .email(trainer.getEmail())
                    .name(trainer.getName())
                    .profile(ProfileDto.from(trainer.getMemberProfile()))
                    .gym(GymDto.from(trainer.getGym()))
                    .build();
        }
    }
}
