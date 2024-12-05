package com.konai.fxs.v1.transaction.controller

import com.jayway.jsonpath.JsonPath
import com.konai.fxs.common.enumerate.*
import com.konai.fxs.common.enumerate.AcquirerType.FX_DEPOSIT
import com.konai.fxs.common.enumerate.AcquirerType.FX_PURCHASER
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.TestCommonFunctions.postProc
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import com.konai.fxs.testsupport.TestExtensionFunctions.toModel
import com.konai.fxs.testsupport.annotation.CustomSpringBootTest
import com.konai.fxs.v1.account.service.V1AccountFindService
import com.konai.fxs.v1.account.service.V1AccountSaveService
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate
import com.konai.fxs.v1.transaction.service.V1TransactionFindService
import com.konai.fxs.v1.transaction.service.V1TransactionWithdrawalService
import com.konai.fxs.v1.transaction.service.cache.V1TransactionCacheService
import com.konai.fxs.v1.transaction.service.domain.V1TransactionPredicate
import io.kotest.matchers.bigdecimal.shouldBeGreaterThanOrEquals
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.springframework.test.web.servlet.MockMvc
import java.math.BigDecimal

@CustomSpringBootTest
class V1AccountTransactionControllerTest(
    private val mockMvc: MockMvc,
    private val v1AccountSaveService: V1AccountSaveService,
    private val v1AccountFindService: V1AccountFindService,
    private val v1TransactionFindService: V1TransactionFindService,
    private val v1TransactionCacheService: V1TransactionCacheService,
    private val v1TransactionWithdrawalService: V1TransactionWithdrawalService
) : CustomBehaviorSpec({

    val v1AccountFixture = dependencies.v1AccountFixture
    val v1TransactionFixture = dependencies.v1TransactionFixture
    val v1TransactionManualDepositRequestFixture = dependencies.v1TransactionManualDepositRequestFixture
    val v1TransactionManualWithdrawalRequestFixture = dependencies.v1TransactionManualWithdrawalRequestFixture
    val v1TransactionWithdrawalRequestFixture = dependencies.v1TransactionWithdrawalRequestFixture
    val v1TransactionWithdrawalCompleteRequestFixture = dependencies.v1TransactionWithdrawalCompleteRequestFixture

    fun saveAccount(account: V1Account, balance: BigDecimal = account.balance): V1Account {
        return v1AccountSaveService.save(account.update(V1AccountPredicate(balance = balance)))
    }

    given("외화 걔좌 '수기 입금' 거래 API 요청하여") {
        val url = "/api/v1/account/transaction/manual/deposit"

        `when`("외화 계좌 정보 없는 경우") {
            val request = v1TransactionManualDepositRequestFixture.make()
            val result = mockMvc.postProc(url, request)

            then("'404 Not Found - 210_2001_001' 에러 응답 정상 확인한다") {
                result.andExpect {
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
        val fromAccount = saveAccount(v1AccountFixture.make(acquirerType = FX_PURCHASER))
        val toAccount = saveAccount(v1AccountFixture.make(acquirerType = FX_DEPOSIT))

        val fromAcquirer = fromAccount.acquirer.toModel()
        val toAcquirer = toAccount.acquirer.toModel()
        val request = v1TransactionManualDepositRequestFixture.make(fromAcquirer, toAcquirer)

        `when`("'USD 100' 수기 입금 처리 결과 성공인 경우") {
            val result = mockMvc.postProc(url, request)

            then("'200 Ok' 성공 응답 정상 확인한다") {
                result.andExpect {
                    status { isOk() }
                    content {
                        jsonPath("transactionId", greaterThanOrEqualTo(1))
                    }
                }
            }

            then("외화 계좌 잔액 증액 & 평균 환율 업데이트 정상 확인한다") {
                val account = v1AccountFindService.findByAcquirer(toAccount.acquirer, toAccount.currency)
                account!! shouldNotBe null
                account.balance.toDouble() shouldBe 100
                account.averageExchangeRate.toDouble() shouldBe 1000.00
            }

            then("외화 계좌 '수기 입금' 거래 내역 저장 정보 정상 확인한다") {
                val transactionId = JsonPath.read<Int>(result.andReturn().response.contentAsString, "transactionId")
                val predicate = V1TransactionPredicate(id = transactionId.toLong())
                val transaction = v1TransactionFindService.findByPredicate(predicate)!!
                transaction.type shouldBe TransactionType.DEPOSIT
                transaction.purpose shouldBe TransactionPurpose.DEPOSIT
                transaction.status shouldBe TransactionStatus.COMPLETED
            }
        }
    }

    given("외화 계좌 '수기 출금' 거래 API 요청하여") {
        val url = "/api/v1/account/transaction/manual/withdrawal"

        // 외화 계좌 정보 저장
        val fromAccount = saveAccount(v1AccountFixture.make(acquirerType = FX_DEPOSIT))
        val amount = BigDecimal(100)
        val request = v1TransactionManualWithdrawalRequestFixture.make(fromAcquirer = fromAccount.acquirer.toModel(), amount = amount)

        `when`("출금 요청 금액보다 외화 계좌 잔액 부족한 경우") {
            val result = mockMvc.postProc(url, request)

            then("'500 Internal Server Error - 210_2001_005' 에러 응답 정상 확인한다") {
                result.andExpect {
                    status { isInternalServerError() }
                    content {
                        jsonPath("result.status", equalTo(ResultStatus.FAILED.name))
                        jsonPath("result.code", equalTo("210_2001_005"))
                        jsonPath("result.message", equalTo("Account transaction service failed. Account balance is insufficient."))
                    }
                }
            }
        }

        saveAccount(fromAccount, balance = BigDecimal(100))

        `when`("'USD 100' 수기 출금 처리 결과 성공인 경우") {
            val result = mockMvc.postProc(url, request)

            then("'200 OK' 성공 응답 정상 확인한다") {
                result.andExpect {
                    status { isOk() }
                    content {
                        jsonPath("transactionId", greaterThanOrEqualTo(1))
                    }
                }
            }

            then("외화 계좌 잔액 감액 업데이트 정상 확인한다") {
                val account = v1AccountFindService.findByAcquirer(fromAccount.acquirer, fromAccount.currency)
                account!! shouldNotBe null
                account.balance.toDouble() shouldBe 0
            }

            then("외화 계좌 '수기 출금' 거래 내역 저장 정보 정상 확인한다") {
                val transactionId = JsonPath.read<Int>(result.andReturn().response.contentAsString, "transactionId")
                val predicate = V1TransactionPredicate(id = transactionId.toLong())
                val transaction = v1TransactionFindService.findByPredicate(predicate)!!
                transaction.type shouldBe TransactionType.WITHDRAWAL
                transaction.purpose shouldBe TransactionPurpose.WITHDRAWAL
                transaction.status shouldBe TransactionStatus.COMPLETED
            }
        }
    }

    given("외화 계좌 '출금' 거래 API 요청하여 ") {
        val url = "/api/v1/account/transaction/withdrawal"
        val account = saveAccount(v1AccountFixture.make(acquirerType = FX_DEPOSIT))
        val amount = BigDecimal(100)
        val request = v1TransactionWithdrawalRequestFixture.make(acquirer = account.acquirer.toModel(), amount = amount)

        `when`("출금 요청 금액보다 외화 계좌 잔액 부족한 경우") {
            val result = mockMvc.postProc(url, request)

            then("'500 Internal Server Error - 210_2001_005' 에러 응답 정상 확인한다") {
                result.andExpect {
                    status { isInternalServerError() }
                    content {
                        jsonPath("result.status", equalTo(ResultStatus.FAILED.name))
                        jsonPath("result.code", equalTo("210_2001_005"))
                        jsonPath("result.message", equalTo("Account transaction service failed. Account balance is insufficient."))
                    }
                }
            }
        }

        saveAccount(account, balance = BigDecimal(100))

        `when`("'USD 100' 출금 요청 처리 결과 성공인 경우") {
            val result = mockMvc.postProc(url, request)

            then("'200 OK' 성공 응답 정상 확인한다") {
                result.andExpect {
                    status { isOk() }
                    content {
                        jsonPath("trReferenceId", equalTo(request.trReferenceId))
                    }
                }
            }

            then("외화 계좌 출금 거래 Cache 정보 생성 정상 확인한다") {
                val trReferenceId = request.trReferenceId
                val channel = request.channel
                v1TransactionCacheService.hasPreparedWithdrawalTransactionCache(trReferenceId, channel) shouldBe true
            }

            then("외화 계좌 출금 거래 금액 합계 Cache 정보 업데이트 정상 확인한다") {
                val acquirer = account.acquirer
                v1TransactionCacheService.findPreparedWithdrawalTotalAmountCache(acquirer) shouldBeGreaterThanOrEquals BigDecimal(100)
            }

            then("외화 계좌 출금 거래 내역 저장 정보 정상 확인한다") {
                val trReferenceId = JsonPath.read<String>(result.andReturn().response.contentAsString, "trReferenceId")
                val predicate = V1TransactionPredicate(trReferenceId = trReferenceId)
                val transaction = v1TransactionFindService.findByPredicate(predicate)!!
                transaction.type shouldBe TransactionType.WITHDRAWAL
                transaction.purpose shouldBe TransactionPurpose.WITHDRAWAL
                transaction.status shouldBe TransactionStatus.PENDING
            }
        }
    }

    given("외화 계좌 '출금 완료' 거래 API 요청하여 ") {
        val url = "/api/v1/account/transaction/withdrawal/complete"
        val account = saveAccount(v1AccountFixture.make(acquirerType = FX_DEPOSIT, balance = 100))
        val acquirer = account.acquirer
        val trReferenceId = generateUUID()
        val channel = TransactionChannel.ORS
        val request = v1TransactionWithdrawalCompleteRequestFixture.make(trReferenceId, channel)

        `when`("외화 계좌 출금 거래가 없는 경우") {
            val result = mockMvc.postProc(url, request)

            then("'400 Bad Request - 210_2001_007' 에러 응답 정상 확인한다") {
                result.andExpect {
                    status { isBadRequest() }
                    content {
                        jsonPath("result.status", equalTo(ResultStatus.FAILED.name))
                        jsonPath("result.code", equalTo("210_2001_007"))
                        jsonPath("result.message", equalTo("Account transaction service failed. Withdrawal transaction not found."))
                    }
                }
            }
        }

        // 출금 거래 정보 저장
        val transaction = v1TransactionFixture.prepareWithdrawalTransaction(acquirer, trReferenceId, BigDecimal(100))
        v1TransactionWithdrawalService.withdrawal(transaction)

        // 출금 거래 금액 합계 추가분 증액
        v1TransactionCacheService.incrementPreparedWithdrawalTotalAmountCache(acquirer, BigDecimal(100))

        `when`("외화 계좌 잔액 부족한 경우") {
            val result = mockMvc.postProc(url, request)

            then("'500 Internal Server Error - 210_2001_005' 에러 응답 정상 확인한다") {
                result.andExpect {
                    status { isInternalServerError() }
                    content {
                        jsonPath("result.status", equalTo(ResultStatus.FAILED.name))
                        jsonPath("result.code", equalTo("210_2001_005"))
                        jsonPath("result.message", equalTo("Account transaction service failed. Account balance is insufficient."))
                    }
                }
            }
        }

        saveAccount(account, balance = BigDecimal(1000))

        `when`("'USD 100' 출금 완료 요청 처리 결과 성공인 경우") {
            val result = mockMvc.postProc(url, request)

            then("'200 OK' 성공 응답 정상 확인한다") {
                result.andExpect {
                    status { isOk() }
                    content {
                        jsonPath("trReferenceId", equalTo(request.trReferenceId))
                    }
                }
            }

            then("외화 계좌 출금 거래 Cache 정보 삭제 정상 확인한다") {
                v1TransactionCacheService.hasPreparedWithdrawalTransactionCache(trReferenceId, channel) shouldBe false
            }

            then("외화 계좌 출금 거래 금액 합계 Cache 정보 업데이트 정상 확인한다") {
                v1TransactionCacheService.findPreparedWithdrawalTotalAmountCache(acquirer) shouldBeGreaterThanOrEquals BigDecimal(100)
            }
        }
    }

})