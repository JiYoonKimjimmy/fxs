package com.konai.fxs.v1.sequence.repository

import com.konai.fxs.common.enumerate.SequenceType
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.annotation.CustomDataJpaTest
import com.konai.fxs.v1.sequence.service.domain.V1SequenceGeneratorMapper
import io.kotest.matchers.shouldBe

@CustomDataJpaTest
class V1SequenceGeneratorRepositoryImplTest(
    private val v1SequenceGeneratorJpaRepository: V1SequenceGeneratorJpaRepository
) : CustomBehaviorSpec({

    val v1SequenceGeneratorRepository = V1SequenceGeneratorRepositoryImpl(V1SequenceGeneratorMapper(), v1SequenceGeneratorJpaRepository)

    given("신규 Sequence 값 증가 요청하여") {
        val type = SequenceType.TRANSACTION_SEQUENCE

        `when`("Sequence 정보 신규 생성인 경우") {
            val result = v1SequenceGeneratorRepository.next(type)

            then("Sequence 'value : 1' 정상 확인한다") {
                result.value shouldBe 1
            }
        }
    }

    given("기존 Sequence 값 증가 요청하여") {
        val type = SequenceType.TRANSACTION_SEQUENCE
        
        // Sequence 전체 삭제
        v1SequenceGeneratorJpaRepository.deleteAll()
        // Sequence '1회' 증가 처리
        v1SequenceGeneratorRepository.next(type)

        `when`("Sequence 정보 정상 조회인 경우") {
            val result = v1SequenceGeneratorRepository.next(type)

            then("Sequence 'value : 2' 정상 확인한다") {
                result.value shouldBe 2
            }
        }
    }

})