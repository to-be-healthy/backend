package com.tobe.healthy.kotest.study

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.maps.haveKeys
import io.kotest.matchers.maps.haveValues
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.haveLength
import io.kotest.matchers.string.startWith

class KotestStudy : StringSpec({

    "문자열 길이를 체크한다." {
        "hello world" shouldBe haveLength(11)
    }

    "두 문자열이 일치하는지 확인한다." {
        "hello world!" shouldBe "hello world!"
    }

    "1. 리스트 원소가 비었는지 확인한다." {
        val strings = mutableListOf<String>()
        strings should beEmpty<String>()
    }

    "2. 리스트 원소가 비었는지 확인한다." {
        val strings = listOf<String>()
        strings should beEmpty<String>()
    }

    "1. 해당 원소가 포함 돼 있는지 확인한다." {
        val maps = mapOf("네" to 1, "카" to 2, "라" to 3, "쿠" to 4, "배" to 5)
        maps should haveKeys("네", "카", "라")
    }

    "2. 해당 원소가 포함 돼 있는지 확인한다." {
        val maps = mapOf("네" to 1, "카" to 2, "라" to 3, "쿠" to 4, "배" to 5)
        maps should haveValues(1, 2, 3)
    }

    "1. set 동등성 비교를 한다" {
        val set = setOf("apple", "banana", "cherry")
        set shouldNotBe setOf("apple", "banana")
    }

    "예외가 정확히 발생했는지 확인한다." {
        val exception = shouldThrow<IllegalArgumentException> {
            throw IllegalArgumentException("예외가 발생하였습니다.")
        }
        exception.message should startWith("예외가")
    }
})