package com.konai.fxs.v1.account.repository

import com.konai.fxs.common.EMPTY
import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.jdsl.findOne
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.CustomDataJpaTest
import com.konai.fxs.testsupport.TestExtensionFunctions
import com.konai.fxs.v1.account.repository.entity.V1AccountEntity.V1AcquirerEntity
import com.konai.fxs.v1.account.repository.jdsl.V1AccountJdslExecutor
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate.V1AcquirerPredicate
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.security.SecureRandom

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

    given("'id' 조건 외화 계좌 Entity 정보 조회 요청하여") {
        val entity = v1AccountJpaRepository.save(v1AccountEntityFixture.make())
        val id = entity.id!!
        val predicate = V1AccountPredicate(id = id)
        val executor = V1AccountJdslExecutor(predicate)

        `when`("일치한 Entity 정보 있는 경우") {
            val result = v1AccountJpaRepository.findOne(executor.selectQuery())

            then("조회 결과 'id' 일치 정상 확인한다") {
                result!! shouldNotBe null
                result.id shouldBe id
            }
        }
    }

    given("'acquirerId' & 'acquirerType' 조건 외화 계좌 Entity 정보 조회 요청하여") {
        val entity = v1AccountJpaRepository.save(v1AccountEntityFixture.make())
        val acquirer = V1AcquirerPredicate(entity.acquirer.id, entity.acquirer.type, EMPTY)
        val predicate = V1AccountPredicate(acquirer = acquirer)
        val executor = V1AccountJdslExecutor(predicate)

        `when`("일치한 Entity 정보 있는 경우") {
            val result = v1AccountJpaRepository.findOne(executor.selectQuery())

            then("조회 결과 'id' 일치 정상 확인한다") {
                result!! shouldNotBe null
                result.id shouldBe entity.id
            }
        }
    }

    given("'acquirerId' & 'acquirerType' & 'acquirerName' 조건 외화 계좌 Entity 정보 조회 요청하여") {
        val entity = v1AccountJpaRepository.save(v1AccountEntityFixture.make())
        val acquirer = V1AcquirerPredicate(entity.acquirer.id, entity.acquirer.type, entity.acquirer.name)
        val predicate = V1AccountPredicate(acquirer = acquirer)
        val executor = V1AccountJdslExecutor(predicate)

        `when`("일치한 Entity 정보 있는 경우") {
            val result = v1AccountJpaRepository.findOne(executor.selectQuery())

            then("조회 결과 'id' 일치 정상 확인한다") {
                result!! shouldNotBe null
                result.id shouldBe entity.id
            }
        }

        `when`("일치한 Entity 정보가 요청 'id' 정보와 같은 경우") {
            val result = v1AccountJpaRepository.findOne(executor.selectQuery())?.let { it.id == entity.id } ?: false

            then("'true' 결과 정상 확인한다") {
                result shouldBe true
            }
        }

        `when`("일치한 Entity 정보가 요청 'id' 정보와 다른 경우") {
            val newAccountId = SecureRandom().nextLong()
            val result = v1AccountJpaRepository.findOne(executor.selectQuery())?.let { it.id == newAccountId } ?: false

            then("'false' 결과 정상 확인한다") {
                result shouldBe false
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