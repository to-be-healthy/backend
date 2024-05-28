package com.tobe.healthy.push.domain.entity

import com.tobe.healthy.common.BaseTimeEntity
import com.tobe.healthy.member.domain.entity.Member
import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY

@Entity
class MemberToken(

    var token: String,

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    val member: Member? = null,

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_token_id")
    val id: Long = 0
) : BaseTimeEntity<MemberToken, Long>() {

    fun changeToken(token: String) {
        this.token = token
    }

    companion object {
        fun register(member: Member, token: String): MemberToken {
            return MemberToken(
                member = member,
                token = token
            )
        }
    }
}