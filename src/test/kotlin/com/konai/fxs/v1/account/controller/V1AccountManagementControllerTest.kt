package com.konai.fxs.v1.account.controller

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
        val accountNumber = TestExtensionFunctions.generateUUID()
        val request = V1CreateAccountRequest(accountNumber = accountNumber)
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
                            jsonPath("data.accountNumber", equalTo(accountNumber))
                        }
                    }
            }
        }
    }

})