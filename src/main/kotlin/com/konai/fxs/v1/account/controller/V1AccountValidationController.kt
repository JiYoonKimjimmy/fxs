package com.konai.fxs.v1.account.controller

import com.konai.fxs.v1.account.controller.model.V1CheckLimitAccountRequest
import com.konai.fxs.v1.account.controller.model.V1CheckLimitAccountResponse
import com.konai.fxs.v1.account.service.V1AccountValidationService
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RequestMapping("/api/v1/account")
@RestController
class V1AccountValidationController(
    private val v1AccountValidationService: V1AccountValidationService
) {

    @PostMapping("/limit")
    fun checkLimit(@RequestBody request: V1CheckLimitAccountRequest): ResponseEntity<V1CheckLimitAccountResponse> {
        return v1AccountValidationService.checkLimit(
                acquirer = V1Acquirer(request.acquirerId, request.acquirerType),
                currency = request.currency,
                amount = BigDecimal(request.amount)
            )
            .let { V1CheckLimitAccountResponse() }
            .success(HttpStatus.OK)
    }

}