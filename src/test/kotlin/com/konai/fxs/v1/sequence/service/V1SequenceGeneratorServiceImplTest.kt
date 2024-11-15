package com.konai.fxs.v1.sequence.service

import com.konai.fxs.common.enumerate.SequenceType
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.redis.EmbeddedRedisTestListener
import io.kotest.matchers.shouldBe

class V1SequenceGeneratorServiceImplTest : CustomBehaviorSpec({

    listeners(EmbeddedRedisTestListener())

    val v1SequenceGeneratorRepository = dependencies.fakeV1SequenceGeneratorRepository
    val v1SequenceGeneratorService = dependencies.v1SequenceGeneratorService

    given("신규 Sequence 값 증가 요청하여") {
        val type = SequenceType.TRANSACTION_SEQUENCE

        `when`("Sequence 정보 신규 생성인 경우") {
            val result = v1SequenceGeneratorService.next(type)

            then("결과 '1' 정상 확인한다") {
                result shouldBe 1
            }
        }
    }

    given("기존 Sequence 값 증가 요청하여") {
        val type = SequenceType.TRANSACTION_SEQUENCE

        // Sequence 전체 삭제
        v1SequenceGeneratorRepository.deleteAll()
        // Sequence '1회' 증가 처리
        v1SequenceGeneratorRepository.next(type)

        `when`("Sequence 정보 정상 조회인 경우") {
            val result = v1SequenceGeneratorService.next(type)

            then("결과 '2' 정상 확인한다") {
                result shouldBe 2
            }
        }
    }

})