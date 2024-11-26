package com.konai.fxs.v1.transaction.controller

import com.konai.fxs.v1.transaction.controller.model.V1TransactionManualDepositRequest
import com.konai.fxs.v1.transaction.controller.model.V1TransactionManualDepositResponse
import com.konai.fxs.v1.transaction.service.V1TransactionDepositService
import com.konai.fxs.v1.transaction.service.domain.V1TransactionMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/account/transaction")
@RestController
class V1AccountTransactionController(
    private val v1TransactionMapper: V1TransactionMapper,
    private val v1TransactionDepositService: V1TransactionDepositService
) {

    @PostMapping("/manual/deposit")
    fun manualDeposit(@RequestBody request: V1TransactionManualDepositRequest): ResponseEntity<V1TransactionManualDepositResponse> {
        return v1TransactionMapper.requestToDomain(request)
            .let { v1TransactionDepositService.manualDeposit(it) }
            .let { V1TransactionManualDepositResponse(it.id!!) }
            .success(HttpStatus.OK)
    }

}