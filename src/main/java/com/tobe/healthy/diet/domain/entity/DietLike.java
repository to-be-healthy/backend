package com.tobe.healthy.diet.domain.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;


@Entity
@Table(name = "diet_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class DietLike {

    @EmbeddedId
    private DietLikePK dietLikePK;

    public static DietLike from(DietLikePK dietLikePK) {
        return DietLike.builder()
                .dietLikePK(dietLikePK)
                .build();
    }

}
