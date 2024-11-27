package com.konai.fxs.v1.transaction.controller

import com.konai.fxs.v1.transaction.controller.model.V1TransactionManualDepositRequest
import com.konai.fxs.v1.transaction.controller.model.V1TransactionManualDepositResponse
import com.konai.fxs.v1.transaction.controller.model.V1TransactionManualWithdrawalRequest
import com.konai.fxs.v1.transaction.controller.model.V1TransactionManualWithdrawalResponse
import com.konai.fxs.v1.transaction.service.V1TransactionDepositService
import com.konai.fxs.v1.transaction.service.V1TransactionWithdrawalService
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
    private val v1TransactionDepositService: V1TransactionDepositService,
    private val v1TransactionWithdrawalService: V1TransactionWithdrawalService,
) {

    @PostMapping("/manual/deposit")
    fun manualDeposit(@RequestBody request: V1TransactionManualDepositRequest): ResponseEntity<V1TransactionManualDepositResponse> {
        return v1TransactionMapper.requestToDomain(request)
            .let { v1TransactionDepositService.manualDeposit(it) }
            .let { V1TransactionManualDepositResponse(it.id!!) }
            .success(HttpStatus.OK)
    }

    @PostMapping("/manual/withdrawal")
    fun manualWithdrawal(@RequestBody request: V1TransactionManualWithdrawalRequest): ResponseEntity<V1TransactionManualWithdrawalResponse> {
        return v1TransactionMapper.requestToDomain(request)
            .let { v1TransactionWithdrawalService.manualWithdrawal(it) }
            .let { V1TransactionManualWithdrawalResponse(it.id!!) }
            .success(HttpStatus.OK)
    }

}