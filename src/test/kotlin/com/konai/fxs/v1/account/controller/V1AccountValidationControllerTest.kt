package com.konai.fxs.v1.account.controller

import com.konai.fxs.common.Currency.USD
import com.konai.fxs.common.enumerate.AcquirerType.FX_DEPOSIT
import com.konai.fxs.common.enumerate.ResultStatus
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.TestCommonFunctions.postProc
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import com.konai.fxs.testsupport.annotation.CustomSpringBootTest
import com.konai.fxs.v1.account.controller.model.V1CheckLimitAccountRequest
import com.konai.fxs.v1.account.repository.V1AccountJpaRepository
import org.hamcrest.Matchers.equalTo
import org.springframework.test.web.servlet.MockMvc

@CustomSpringBootTest
class V1AccountValidationControllerTest(
    private val mockMvc: MockMvc,
    private val v1AccountJpaRepository: V1AccountJpaRepository
) : CustomBehaviorSpec({

    val v1AccountEntityFixture = dependencies.v1AccountEntityFixture

    val transactionCacheRepository = dependencies.transactionCacheRepository

    given("외화 계좌 한도 확인 API 요청되어") {
        val url = "/api/v1/account/limit"

        `when`("외화 계좌 정보 없는 경우") {
            val request = V1CheckLimitAccountRequest(generateUUID(), FX_DEPOSIT, USD, 10000)
            val result = mockMvc.postProc(url, request)

            then("'404 Not Found - 210_1001_001' 에러 응답 정상 확인한다") {
                result.andExpect {
                    status { isNotFound() }
                    content {
                        jsonPath("result.status", equalTo(ResultStatus.FAILED.name))
                        jsonPath("result.code", equalTo("210_1002_001"))
                        jsonPath("result.message", equalTo("Account validation service failed. Account not found."))
                    }
                }
            }
        }

        // 잔액 없는 외화 계좌 DB 저장
        val noBalanceAccount = v1AccountJpaRepository.save(v1AccountEntityFixture.make())

        `when`("외화 계좌 잔액 부족 한도 초과인 경우") {
            val request = V1CheckLimitAccountRequest(noBalanceAccount.acquirer.id, noBalanceAccount.acquirer.type, noBalanceAccount.currency, 10000)
            val result = mockMvc.postProc(url, request)

            then("'500 Internal server error - 210_1002_005' 에러 응답 정상 확인한다") {
                result.andExpect {
                    status { isInternalServerError() }
                    content {
                        jsonPath("result.status", equalTo(ResultStatus.FAILED.name))
                        jsonPath("result.code", equalTo("210_1002_005"))
                        jsonPath("result.message", equalTo("Account validation service failed. Account balance is insufficient."))
                        jsonPath("result.detailMessage", equalTo("balance: 0 < (preparedAmount: 0 + amount: 10000)"))
                    }
                }
            }
        }

        // 외화 계좌 DB 저장
        val account = v1AccountJpaRepository.save(v1AccountEntityFixture.make(balance = 20000))
        // 출금 준비 합계 Cache 저장
        transactionCacheRepository.incrementWithdrawalPreparedTotalAmountCache(account.acquirer.id, account.acquirer.type, 10001)

        `when`("외화 계좌 출금 준비 합계 한도 초과인 경우") {
            val request = V1CheckLimitAccountRequest(account.acquirer.id, account.acquirer.type, account.currency, 10000)
            val result = mockMvc.postProc(url, request)

            then("'500 Internal server error - 210_1002_005' 에러 응답 정상 확인한다") {
                result.andExpect {
                    status { isInternalServerError() }
                    content {
                        jsonPath("result.status", equalTo(ResultStatus.FAILED.name))
                        jsonPath("result.code", equalTo("210_1002_005"))
                        jsonPath("result.message", equalTo("Account validation service failed. Account balance is insufficient."))
                        jsonPath("result.detailMessage", equalTo("balance: 20000 < (preparedAmount: 10001 + amount: 10000)"))
                    }
                }
            }
        }

        // 출금 준비 합계 Cache 초기화
        transactionCacheRepository.clearWithdrawalPreparedTotalAmountCache(account.acquirer.id, account.acquirer.type)

        `when`("외화 계좌 한도 확인 결과 정상인 경우") {
            val request = V1CheckLimitAccountRequest(account.acquirer.id, account.acquirer.type, account.currency, 10000)
            val result = mockMvc.postProc(url, request)

            then("'200 Ok' 성공 응답 정상 확인한다") {
                result.andExpect {
                    status { isOk() }
                    content {
                        jsonPath("result.status", equalTo(ResultStatus.SUCCESS.name))
                    }
                }
            }
        }
    }

})