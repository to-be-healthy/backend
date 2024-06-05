package com.tobe.healthy.gym.domain.entity

import com.tobe.healthy.common.BaseTimeEntity
import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.JOIN_CODE_NOT_VALID
import com.tobe.healthy.member.domain.entity.Member
import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY
import lombok.ToString
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicUpdate
@ToString
class Gym(
    val name: String,

    @Column(length = 6)
    val joinCode: String,

    @OneToMany(fetch = LAZY, mappedBy = "gym")
    @ToString.Exclude
    val member: MutableList<Member> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "gym_id")
    val id: Long? = null,

) : BaseTimeEntity<Gym, Long>() {

    fun validateJoinCode(joinCode: String?) {
        if (this.joinCode != joinCode) {
            throw CustomException(JOIN_CODE_NOT_VALID)
        }
    }

    companion object {
        fun registerGym(name: String, accessKey: String): Gym {
            return Gym(
                name = name,
                joinCode = accessKey
            )
        }
    }
}
