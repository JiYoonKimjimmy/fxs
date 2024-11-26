package com.konai.fxs.v1.transaction.controller

import com.jayway.jsonpath.JsonPath
import com.konai.fxs.common.enumerate.AcquirerType.FX_DEPOSIT
import com.konai.fxs.common.enumerate.AcquirerType.FX_PURCHASER
import com.konai.fxs.common.enumerate.ResultStatus
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.TestDependencies.objectMapper
import com.konai.fxs.testsupport.TestExtensionFunctions.toPredicate
import com.konai.fxs.testsupport.annotation.CustomSpringBootTest
import com.konai.fxs.v1.account.service.V1AccountFindService
import com.konai.fxs.v1.account.service.V1AccountSaveService
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.transaction.service.V1TransactionFindService
import com.konai.fxs.v1.transaction.service.domain.V1TransactionPredicate
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@CustomSpringBootTest
class V1AccountTransactionControllerTest(
    private val mockMvc: MockMvc,
    private val v1AccountSaveService: V1AccountSaveService,
    private val v1AccountFindService: V1AccountFindService,
    private val v1TransactionFindService: V1TransactionFindService
) : CustomBehaviorSpec({

    val v1TransactionManualDepositRequestFixture = dependencies.v1TransactionManualDepositRequestFixture
    val v1AccountFixture = dependencies.v1AccountFixture

    fun saveAccount(vararg accounts: V1Account) {
        accounts.forEach(v1AccountSaveService::save)
    }

    given("외화 걔좌 '수기 입금' 거래 API 요청하여") {
        val url = "/api/v1/account/transaction/manual/deposit"

        `when`("외화 계좌 정보 없는 경우") {
            val request = v1TransactionManualDepositRequestFixture.make()
            val result = mockMvc
                .post(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andDo { print() }

            then("'404 Not Found - 210_2001_001' 에러 응답 정상 확인한다") {
                result
                    .andExpect {
                        status { isNotFound() }
                        content {
                            jsonPath("result.status", equalTo(ResultStatus.FAILED.name))
                            jsonPath("result.code", equalTo("210_2001_001"))
                            jsonPath("result.message", equalTo("Account transaction service failed. Account not found."))
                        }
                    }
            }
        }

        // 외화 구입처/예치금 계좌 정보 저장
        val fromAccount = v1AccountFixture.make(acquirerType = FX_PURCHASER)
        val toAccount = v1AccountFixture.make(acquirerType = FX_DEPOSIT)
        saveAccount(fromAccount, toAccount)

        val fromAcquirer = fromAccount.acquirer.toPredicate()
        val toAcquirer = toAccount.acquirer.toPredicate()
        val request = v1TransactionManualDepositRequestFixture.make(fromAcquirer, toAcquirer)

        `when`("'USD 100' 수기 입금 처리 결과 성공인 경우") {
            val result = mockMvc
                .post(url) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andDo { print() }

            then("'200 Ok' 성공 응답 정상 확인한다") {
                result
                    .andExpect {
                        status { isOk() }
                        content {
                            jsonPath("transactionId", greaterThanOrEqualTo(1))
                        }
                    }
            }

            then("외화 예치금 계좌 잔액 증액 & 평균 환율 업데이트 정상 확인한다") {
                val account = v1AccountFindService.findByAcquirer(toAccount.acquirer, toAccount.currency)
                account!! shouldNotBe null
                account.balance.toDouble() shouldBe 100
                account.averageExchangeRate.toDouble() shouldBe 1000.00
            }

            then("외화 계좌 '수기 입금' 거래 내역 저장 정보 정상 확인한다") {
                val transactionId = JsonPath.read<Int>(result.andReturn().response.contentAsString, "transactionId")
                val predicate = V1TransactionPredicate(id = transactionId.toLong())
                shouldNotThrowAny { v1TransactionFindService.findByPredicate(predicate) }
            }
        }
    }

})
