package com.konai.fxs.v1.exchangerate.koreaexim.repository

import com.konai.fxs.common.jdsl.findAll
import com.konai.fxs.common.jdsl.findOne
import com.konai.fxs.common.model.PageableRequest
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.annotation.CustomDataJpaTest
import com.konai.fxs.v1.exchangerate.koreaexim.repository.jdsl.V1KoreaeximExchangeRateJdslExecutor
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRatePredicate
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

@CustomDataJpaTest
class V1KoreaeximExchangeRateRepositoryImplTest(
    private val v1KoreaeximExchangeRateJpaRepository: V1KoreaeximExchangeRateJpaRepository
) : CustomBehaviorSpec({

    val v1KoreaeximExchangeRateEntityFixture = dependencies.v1KoreaeximExchangeRateEntityFixture

    given("한국수출입은행 환율 정보 다건 저장 요청하여") {
        val entities = listOf(v1KoreaeximExchangeRateEntityFixture.make())

        `when`("저장 처리 성공인 경우") {
            val result = v1KoreaeximExchangeRateJpaRepository.saveAll(entities)

            then("처리 결과 정상 확인한다") {
                result.shouldNotBeEmpty()
                result.shouldHaveSize(1)
            }
        }
    }

    given("한국수출입은행 환율 정보 `registerDate`, `cur_unit` 기준 조회 요청하여") {
        val entity = v1KoreaeximExchangeRateEntityFixture.make()
        val predicate = V1KoreaeximExchangeRatePredicate(registerDate = entity.registerDate, curUnit = entity.curUnit)
        val executor = V1KoreaeximExchangeRateJdslExecutor(predicate)

        // 한국수출입은행 환율 정보 저장
        v1KoreaeximExchangeRateJpaRepository.save(v1KoreaeximExchangeRateEntityFixture.make())

        `when`("정상 정보 등록된 경우") {
            val result = v1KoreaeximExchangeRateJpaRepository.findOne(executor.selectQuery())

            then("조회 결과 정상 확인한다") {
                result!! shouldNotBe null
            }
        }
    }

    given("한국수출수입은행 환율 정보 'registerDate', 'cur_unit' 기준 마지막 정보 조회 요청하여") {
        val entity1 = v1KoreaeximExchangeRateEntityFixture.make(index = 1, ttb = "1000")
        val entity2 = v1KoreaeximExchangeRateEntityFixture.make(index = 2, ttb = "900")
        val predicate = V1KoreaeximExchangeRatePredicate(registerDate = entity1.registerDate, curUnit = entity1.curUnit)
        val pageable = PageableRequest(number = 0, size = 1, sortBy = "index")
        val executor = V1KoreaeximExchangeRateJdslExecutor(predicate)

        // 한국수출입은행 환율 정보 저장
        v1KoreaeximExchangeRateJpaRepository.saveAll(listOf(entity1, entity2))

        `when`("정상 정보 등록된 경우") {
            val result = v1KoreaeximExchangeRateJpaRepository.findAll(pageable, executor.selectQuery())

            then("조회 결과 정상 확인한다") {
                result.totalElements shouldBe 2
                result.content.shouldNotBeEmpty()
                result.content.shouldHaveSize(1)

                val content = result.content.first()
                content!! shouldNotBe null
                content.index shouldBe 2
                content.ttb shouldBe "900"
            }
        }
    }

})