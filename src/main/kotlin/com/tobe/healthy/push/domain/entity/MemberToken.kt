package com.tobe.healthy.push.domain.entity

import com.tobe.healthy.common.BaseTimeEntity
import com.tobe.healthy.member.domain.entity.Member
import jakarta.persistence.*
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY
import lombok.ToString

@Entity
class MemberToken(

    @Enumerated(STRING)
    var deviceType: DeviceType,

    var token: String,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    val member: Member? = null,

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_token_id")
    val id: Long? = null

) : BaseTimeEntity<MemberToken, Long>() {

    fun changeToken(token: String, deviceType: DeviceType) {
        this.token = token
        this.deviceType = deviceType
    }

    companion object {
        fun register(member: Member, token: String, deviceType: DeviceType): MemberToken {
            return MemberToken(
                member = member,
                token = token,
                deviceType = deviceType
            )
        }
    }
}