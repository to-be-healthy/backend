package com.study.effectiveKotlin

import com.tobe.healthy.log
import io.kotest.core.spec.style.StringSpec

class ObjectCopy(

) : StringSpec({

    "객체를 복사한다" {
        val memberInfo = MemberInfo(name = "정선우", age = 27)
        val updateMemberInfo = memberInfo.copy(age = 37)

        log.info { "memberInfo => ${memberInfo}" }
        log.info { "updateMemberInfo => ${updateMemberInfo}" }
    }

    "객체의 필드를 대상으로 null체크를 한다" {
        val memberInfo = MemberInfo(name = " ", age = 27)
//        memberInfo.name.isNullOrBlank() shouldBe true
//        memberInfo.name.isNullOrEmpty() shouldBe true
//        MemberInfo(name = "정선우", age = 27)
//        val memberInfos: MutableList<MemberInfo> = mutableListOf()
//        memberInfos.add(MemberInfo(name = "정선우", age = 27))
//
//        val nicknames: MutableList<String>? = mutableListOf()
//        nicknames.add(null)
//
//        MemberInfo(name = "", age = 27, nicknames = )
//
//        val emptyMemberInfos: MutableList<MemberInfo> = mutableListOf()
//
//        log.info { "memberInfos.isNullOrEmpty() => " + memberInfos.isNullOrEmpty() }
//        log.info { "emptyMemberInfos.isNullOrEmpty() => " + emptyMemberInfos.isNullOrEmpty() }
    }
})

data class MemberInfo(
    val name: String,
    val age: Int,
    val nicknames: MutableList<String>? = null
)