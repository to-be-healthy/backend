package com.tobe.healthy.gym.domain.entity

import com.tobe.healthy.common.BaseTimeEntity
import com.tobe.healthy.config.error.ErrorCode.JOIN_CODE_NOT_VALID
import com.tobe.healthy.member.domain.entity.Member
import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicUpdate
class Gym(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "gym_id")
    val id: Long = 0,

    val name: String,

    @Column(length = 6)
    val joinCode: String,

    @OneToMany(fetch = LAZY, mappedBy = "gym")
    val member: List<Member> = mutableListOf()

) : BaseTimeEntity<Gym, Long>() {

    fun validateJoinCode(joinCode: String?) {
        if (this.joinCode != joinCode) {
            JOIN_CODE_NOT_VALID
        }
    }

    companion object {
        fun registerGym(name: String, accessKey: String): Gym {
            if (accessKey.toInt() != 6) {
                throw IllegalArgumentException("헬스장 인증번호의 길이는 6자리여야 합니다.")
            }
            return Gym(
                name = name,
                joinCode = accessKey
            )
        }
    }
}
