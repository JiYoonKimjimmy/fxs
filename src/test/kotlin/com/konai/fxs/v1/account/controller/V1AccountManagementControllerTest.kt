package com.konai.fxs.v1.account.controller

import com.konai.fxs.common.enumerate.AcquirerType.FX_DEPOSIT
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.CustomSpringBootTest
import com.konai.fxs.testsupport.TestExtensionFunctions.generateSequence
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import com.konai.fxs.v1.account.controller.model.V1CreateAccountRequest
import com.konai.fxs.v1.account.controller.model.V1FindOneAccountRequest
import com.konai.fxs.v1.account.repository.V1AccountJpaRepository
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThan
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@CustomSpringBootTest
class V1AccountManagementControllerTest(
    private val mockMvc: MockMvc,
    private val v1AccountJpaRepository: V1AccountJpaRepository
) : CustomBehaviorSpec({

    val objectMapper = dependencies.objectMapper
    val v1AccountEntityFixture = dependencies.v1AccountEntityFixture
    val v1UpdateAccountRequestFixture = dependencies.v1UpdateAccountRequestFixture

    given("외화 계좌 등록 API 요청하여") {
        val createAccountUrl = "/api/v1/account"
        val acquirerId = generateUUID()
        val acquirerType = FX_DEPOSIT
        val acquirerName = "외화 예치금 계좌"
        val currency = "USD"
        val minRequiredBalance = 100000L
        val request = V1CreateAccountRequest(
            acquirerId = acquirerId,
            acquirerType = acquirerType,
            acquirerName = acquirerName,
            currency = currency,
            minRequiredBalance = minRequiredBalance,
        )

        `when`("신규 외화 계좌 정보인 경우") {
            val result = mockMvc
                .post(createAccountUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andDo { print() }

            then("'201 Created' 성공 응답 정상 확인한다") {
                result
                    .andExpect {
                        status { isCreated() }
                        content {
                            jsonPath("data.accountId", greaterThan(0))
                            jsonPath("data.acquirerId", equalTo(acquirerId))
                            jsonPath("data.acquirerType", equalTo(acquirerType.name))
                            jsonPath("data.acquirerName", equalTo(acquirerName))
                            jsonPath("data.currency", equalTo(currency))
                            jsonPath("data.minRequiredBalance", equalTo(minRequiredBalance.toInt()))
                            jsonPath("data.averageExchangeRate", equalTo(0.toDouble()))
                        }
                    }
            }
        }
    }

    given("외화 계좌 단건 API 요청하여") {
        val findOneAccountUrl = "/api/v1/account/one"

        `when`("'accountId' 기준 일치한 외화 계좌 정보 없는 경우") {
            val request = V1FindOneAccountRequest(accountId = generateSequence(), acquirerId = null, acquirerType = null)
            val result = mockMvc
                .post(findOneAccountUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andDo { print() }

            then("'404 Not Found - 210_1000_001' 에러 응답 정상 확인한다") {
                result
                    .andExpect {
                        status { isNotFound() }
                        content {
                            jsonPath("result.status", equalTo("FAILED"))
                            jsonPath("result.code", equalTo("210_1000_001"))
                            jsonPath("result.message", equalTo("Account management service failed. Account not found."))
                        }
                    }
            }
        }

        val entity = v1AccountJpaRepository.save(v1AccountEntityFixture.make())

        `when`("'accountId' 기준 일치한 외화 계좌 정보 있는 경우") {
            val request = V1FindOneAccountRequest(accountId = entity.id, acquirerId = null, acquirerType = null)
            val result = mockMvc
                .post(findOneAccountUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andDo { print() }

            then("조회 결과 응답 정상 확인한다") {
                result
                    .andExpect {
                        status { isOk() }
                        content {
                            jsonPath("data.accountId", equalTo(entity.id?.toInt()))
                            jsonPath("data.acquirerId", equalTo(entity.acquirer.id))
                            jsonPath("data.acquirerType", equalTo(entity.acquirer.type.name))
                            jsonPath("data.acquirerName", equalTo(entity.acquirer.name))
                            jsonPath("data.currency", equalTo(entity.currency))
                            jsonPath("data.minRequiredBalance", equalTo(entity.minRequiredBalance.toInt()))
                            jsonPath("data.averageExchangeRate", equalTo(entity.averageExchangeRate.toDouble()))
                        }
                    }
            }
        }
    }

    given("외화 계좌 변경 API 요청하여") {
        val updateAccountUrl = "/api/v1/account/update"

        `when`("'accountId' 기준 일치한 외화 계좌 정보 없는 경우") {
            val request = v1UpdateAccountRequestFixture.make()
            val result = mockMvc
                .post(updateAccountUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andDo { print() }

            then("'404 Not Found - 210_1000_001' 에러 응답 정상 확인한다") {
                result
                    .andExpect {
                        status { isNotFound() }
                        content {
                            jsonPath("result.status", equalTo("FAILED"))
                            jsonPath("result.code", equalTo("210_1000_001"))
                            jsonPath("result.message", equalTo("Account management service failed. Account not found."))
                        }
                    }
            }
        }

        `when`("'acquirer' 요청 필수 여부 검증 실패하는 경우") {
            val request = v1UpdateAccountRequestFixture.make(acquirerId = generateUUID())
            val result = mockMvc
                .post(updateAccountUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andDo { print() }

            then("'400 Bad Request - 210_1000_905' 에러 응답 정상 확인한다") {
                result
                    .andExpect {
                        status { isBadRequest() }
                        content {
                            jsonPath("result.status", equalTo("FAILED"))
                            jsonPath("result.code", equalTo("210_1000_905"))
                            jsonPath("result.message", equalTo("Account management service failed. Argument not valid."))
                        }
                    }
            }
        }

        val entity = v1AccountJpaRepository.save(v1AccountEntityFixture.make())

        `when`("'accountId' 기준 일치한 외화 계좌 정보 있는 경우") {
            val acquirerId = generateUUID()
            val request = v1UpdateAccountRequestFixture.make(
                accountId = entity.id!!,
                acquirerId = acquirerId,
                acquirerType = entity.acquirer.type,
                acquirerName = entity.acquirer.name
            )
            val result = mockMvc
                .post(updateAccountUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andDo { print() }

            then("변경 결과 응답 정상 확인한다") {
                result
                    .andExpect {
                        status { isOk() }
                        content {
                            jsonPath("data.accountId", equalTo(entity.id?.toInt()))
                            jsonPath("data.acquirerId", equalTo(acquirerId))
                            jsonPath("data.acquirerType", equalTo(entity.acquirer.type.name))
                            jsonPath("data.acquirerName", equalTo(entity.acquirer.name))
                            jsonPath("data.currency", equalTo(entity.currency))
                            jsonPath("data.minRequiredBalance", equalTo(entity.minRequiredBalance.toInt()))
                            jsonPath("data.averageExchangeRate", equalTo(entity.averageExchangeRate.toDouble()))
                        }
                    }
            }
        }
    }

})