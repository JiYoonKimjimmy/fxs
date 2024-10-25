package com.konai.fxs.v1.account.repository

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.jdsl.findByPredicate
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.CustomDataJpaTest
import com.konai.fxs.testsupport.TestExtensionFunctions
import com.konai.fxs.v1.account.repository.entity.V1AcquirerEntity
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

@CustomDataJpaTest
class V1AccountRepositoryImplTest(
    private val v1AccountJpaRepository: V1AccountJpaRepository
) : CustomBehaviorSpec({

    val v1AccountEntityFixture = dependencies.v1AccountEntityFixture

    given("외화 계좌 Entity 정보 저장 요청되어") {
        val acquirerId = TestExtensionFunctions.generateUUID()
        val acquirerType = AcquirerType.FX_DEPOSIT
        val acquirerName = "외화 예치금 계좌"
        val acquirer = V1AcquirerEntity(id = acquirerId, type = acquirerType, name = acquirerName)
        val entity = v1AccountEntityFixture.make(acquirer = acquirer)

        `when`("Entity 정상 정보인 경우") {
            val result = v1AccountJpaRepository.save(entity)

            then("저장 결과 성공 정상 확인한다") {
                result.id!! shouldBeGreaterThan 0L
                result.acquirer.id shouldBe acquirerId
                result.acquirer.name shouldBe acquirerName
            }
        }
    }

    given("외화 계좌 'id' 조건으로 조회 요청되어") {
        // 외화 계좌 정보 DB 저장
        val entity = v1AccountJpaRepository.save(v1AccountEntityFixture.make())

        val id = entity.id!!
        val predicate = V1AccountPredicate(id = id)

        `when`("일치한 Entity 정보 있는 경우") {
            val result = v1AccountJpaRepository.findByPredicate(predicate::generateQuery)!!

            then("조회 결과 'id' 일치 정상 확인한다") {
                result shouldNotBe null
                result.id shouldBe id
            }
        }
    }

    given("'acquirer' 기준 외화 계좌 Entity 존재 여부 조회 요청하여") {
        val acquirerId = TestExtensionFunctions.generateUUID()
        val acquirerType = AcquirerType.FX_DEPOSIT
        val acquirerName = "외화 예치금 계좌"
        val acquirer = V1AcquirerEntity(id = acquirerId, type = acquirerType, name = acquirerName)

        `when`("일치한 Entity 정보가 없는 경우") {
            val result = v1AccountJpaRepository.existsByAcquirer(acquirer)

            then("조회 결과 'false' 정상 확인한다") {
                result shouldBe false
            }
        }

        // 외화 계좌 정보 DB 저장
        val entity = v1AccountEntityFixture.make(acquirer = acquirer)
        v1AccountJpaRepository.save(entity)

        `when`("일치한 Entity 정보 있는 경우") {
            val result = v1AccountJpaRepository.existsByAcquirer(acquirer)

            then("조회 결과 성공 정상 확인한다") {
                result shouldBe true
            }
        }
    }

})