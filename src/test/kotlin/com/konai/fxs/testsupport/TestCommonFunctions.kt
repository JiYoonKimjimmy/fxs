package com.konai.fxs.testsupport

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.konai.fxs.common.enumerate.AccountStatus
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.post
import java.math.BigDecimal

object TestCommonFunctions {

    val objectMapper: ObjectMapper by lazy { jacksonObjectMapper().registerModule(kotlinModule()) }
    private val v1AccountSaveService by lazy { TestDependencies.v1AccountSaveService }

    fun saveAccount(
        account: V1Account,
        balance: BigDecimal? = null,
        averageExchangeRate: BigDecimal? = null,
        status: AccountStatus? = null
    ): V1Account {
        val predicate = V1AccountPredicate(
            balance = balance,
            averageExchangeRate = averageExchangeRate,
            status = status
        )
        return v1AccountSaveService.save(account.update(predicate))
    }

    fun MockMvc.postProc(url: String, request: Any): ResultActionsDsl {
        return post(url) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andDo { print() }
    }

}