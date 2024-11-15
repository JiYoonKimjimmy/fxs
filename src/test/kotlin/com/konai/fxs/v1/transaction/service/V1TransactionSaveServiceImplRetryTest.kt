package com.konai.fxs.v1.transaction.service

import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.annotation.CustomSpringBootTest
import com.konai.fxs.v1.transaction.repository.V1TransactionRepository
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.verify
import org.springframework.beans.factory.annotation.Autowired

@CustomSpringBootTest
class V1TransactionSaveServiceImplRetryTest(
    @Autowired private val v1TransactionSaveService: V1TransactionSaveService,
    @MockkBean private val mockV1TransactionRepository: V1TransactionRepository
) : CustomBehaviorSpec({

    val v1TransactionFixture = dependencies.v1TransactionFixture

    given("외화 계좌 거래 내역 저장 요청되어") {
        val transaction = v1TransactionFixture.make()

        // 전체 실패 Mocking 처리
        every { mockV1TransactionRepository.save(any()) } throws InternalServiceException(ErrorCode.INTERNAL_SERVER_ERROR)

        `when`("DB Connection 에러 '3회' 발생하는 경우") {
            v1TransactionSaveService.save(transaction)

            then("시도 횟수 '3회' 확인한다") {
                verify(exactly = 3) { mockV1TransactionRepository.save(any()) }
            }
        }

        clearMocks(mockV1TransactionRepository)

        // 1회 실패 > 1회 성공 Mocking 처리
        every { mockV1TransactionRepository.save(any()) } throws InternalServiceException(ErrorCode.INTERNAL_SERVER_ERROR) andThen transaction

        `when`("DB Connection 에러 '1회' 발생하는 경우") {
            val result = v1TransactionSaveService.save(transaction)

            then("시도 횟수 '2회' 확인한다") {
                verify(exactly = 2) { mockV1TransactionRepository.save(any()) }
            }

            then("거래 내역 DB 저장 정상 확인한다") {
                result.id shouldBe transaction.id
            }
        }
    }

})