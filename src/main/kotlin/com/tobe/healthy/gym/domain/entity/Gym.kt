package com.tobe.healthy.gym.domain.entity

import com.tobe.healthy.common.BaseTimeEntity
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

    val joinCode: Int,

    @OneToMany(fetch = LAZY, mappedBy = "gym")
    val member: List<Member> = mutableListOf()

) : BaseTimeEntity<Gym, Long>() {
    companion object {
        fun registerGym(name: String, accessKey: Int): Gym {
            return Gym(
                name = name,
                joinCode = accessKey
            )
        }
    }
}
