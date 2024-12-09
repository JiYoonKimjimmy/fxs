package com.konai.fxs.v1.transaction.repository

import com.konai.fxs.common.jdsl.findOne
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.annotation.CustomDataJpaTest
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate.V1AcquirerPredicate
import com.konai.fxs.v1.transaction.repository.jdsl.V1TransactionJdslExecutor
import com.konai.fxs.v1.transaction.service.domain.V1TransactionPredicate
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

@CustomDataJpaTest
class V1TransactionRepositoryImplTest(
    private val v1TransactionJpaRepository: V1TransactionJpaRepository
) : CustomBehaviorSpec({

    val v1TransactionEntityFixture = dependencies.v1TransactionEntityFixture

    given("외화 계좌 거래 내역 Entity 정보 저장 요청되어") {
        val entity = v1TransactionEntityFixture.make()

        `when`("Entity 정상 정보인 경우") {
            val result = v1TransactionJpaRepository.save(entity)

            then("저장 결과 성공 정상 확인한다") {
                result.id!! shouldBeGreaterThan 0L
            }
        }
    }

    given("'baseAcquirer' & 'targetAcquirer' 정보 조건 외화 계좌 거래 내역 Entity 정보 조회 요청하여") {
        val entity = v1TransactionJpaRepository.save(v1TransactionEntityFixture.make())
        val baseAcquirer = V1AcquirerPredicate(entity.baseAcquirer.id, entity.baseAcquirer.type, entity.baseAcquirer.name)
        val targetAcquirer = V1AcquirerPredicate(entity.targetAcquirer?.id, entity.targetAcquirer?.type, entity.targetAcquirer?.name)
        val predicate = V1TransactionPredicate(baseAcquirer = baseAcquirer, targetAcquirer = targetAcquirer)
        val executor = V1TransactionJdslExecutor(predicate)

        `when`("일치한 Entity 정보 있는 경우") {
            val result = v1TransactionJpaRepository.findOne(executor.selectQuery())

            then("조회 결과 'id' 일치 정상 확인한다") {
                result!! shouldNotBe null
                result.id shouldBe entity.id
            }
        }
    }

})