package com.konai.fxs.v1.account.repository

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.CustomDataJpaTest
import com.konai.fxs.testsupport.TestExtensionFunctions
import com.konai.fxs.v1.account.repository.entity.V1AccountEntity
import com.konai.fxs.v1.account.repository.entity.V1AcquirerEntity
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe

@CustomDataJpaTest
class V1AccountRepositoryImplTest(
    private val v1AccountJpaRepository: V1AccountJpaRepository
) : CustomBehaviorSpec({

    val v1AccountEntityFixture = dependencies.v1AccountEntityFixture

    lateinit var saved: V1AccountEntity

    beforeSpec {
        val acquirerId = TestExtensionFunctions.generateUUID()
        val acquirerType = AcquirerType.FX_DEPOSIT
        val acquirerName = "외화 예치금 계좌"
        val acquirer = V1AcquirerEntity(id = acquirerId, type = acquirerType, name = acquirerName)
        val entity = v1AccountEntityFixture.make(acquirer = acquirer)
        saved = v1AccountJpaRepository.save(entity)
    }

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

    given("'acquirer' 기준 외화 계좌 Entity 조회 요청하여") {
        val acquirer = saved.acquirer

        `when`("일치한 Entity 정보 있는 경우") {
            val result = v1AccountJpaRepository.existsByAcquirer(acquirer)

            then("조회 결과 성공 정상 확인한다") {
                result shouldBe true
            }
        }
    }

})