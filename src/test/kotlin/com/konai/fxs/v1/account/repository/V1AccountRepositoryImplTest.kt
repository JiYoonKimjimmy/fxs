package com.konai.fxs.v1.account.repository

import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.CustomDataJpaTest
import com.konai.fxs.testsupport.TestExtensionFunctions
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe

@CustomDataJpaTest
class V1AccountRepositoryImplTest(
    private val v1AccountJpaRepository: V1AccountJpaRepository
) : CustomBehaviorSpec({

    val v1AccountEntityFixture = dependencies.v1AccountEntityFixture

    given("외화 계좌 Entity 정보 등록 요청되어") {
        val accountNumber = TestExtensionFunctions.generateUUID()
        val entity = v1AccountEntityFixture.make(accountNumber = accountNumber)

        `when`("신규 정보인 경우") {
            val result = v1AccountJpaRepository.save(entity)

            then("저장 결과 성공 정상 확인한다") {
                result.id!! shouldBeGreaterThan 0L
                result.accountNumber shouldBe accountNumber
            }
        }
    }

})