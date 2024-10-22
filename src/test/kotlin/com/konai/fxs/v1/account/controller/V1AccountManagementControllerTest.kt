package com.konai.fxs.v1.account.controller

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.CustomSpringBootTest
import com.konai.fxs.testsupport.TestExtensionFunctions
import com.konai.fxs.v1.account.controller.model.V1CreateAccountRequest
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThan
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@CustomSpringBootTest
class V1AccountManagementControllerTest(
    private val mockMvc: MockMvc
) : CustomBehaviorSpec({

    val objectMapper = dependencies.objectMapper

    given("외화 계좌 등록 API 요청하여") {
        val acquirerId = TestExtensionFunctions.generateUUID()
        val acquirerType = AcquirerType.FX_DEPOSIT
        val acquirerName = "외화 예치금 계좌"
        val currency = "USD"
        val minRequiredBalance = 0L
        val request = V1CreateAccountRequest(
            acquirerId = acquirerId,
            acquirerType = acquirerType,
            acquirerName = acquirerName,
            currency = currency,
            minRequiredBalance = minRequiredBalance,
        )
        val createAccountUrl = "/api/v1/account"

        `when`("신규 외화 계좌 정보인 경우") {
            val result = mockMvc
                .post(createAccountUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andDo { print() }

            then("'201 CREATED' 응답 정상 확인한다") {
                result
                    .andExpect {
                        status { isCreated() }
                        content {
                            jsonPath("data.accountId", greaterThan(0))
                            jsonPath("data.acquirerId", equalTo(acquirerId))
                            jsonPath("data.acquirerType", equalTo(acquirerType.name))
                            jsonPath("data.acquirerName", equalTo(acquirerName))
                            jsonPath("data.currency", equalTo(currency))
                            jsonPath("data.minRequiredBalance", equalTo(0))
                            jsonPath("data.averageExchangeRate", equalTo(0.0))
                        }
                    }
            }
        }
    }

})