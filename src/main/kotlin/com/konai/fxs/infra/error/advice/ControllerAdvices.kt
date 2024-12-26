package com.konai.fxs.infra.error.advice

import com.konai.fxs.infra.error.FeatureCode
import com.konai.fxs.infra.error.handler.BaseExceptionHandler
import com.konai.fxs.v1.account.controller.V1AccountManagementController
import com.konai.fxs.v1.account.controller.V1AccountValidationController
import com.konai.fxs.v1.exchangerate.controller.V1ExchangeRateFindController
import com.konai.fxs.v1.transaction.controller.V1AccountTransactionController
import org.springframework.core.annotation.Order
import org.springframework.web.bind.annotation.RestControllerAdvice

@Order(value = 1)
@RestControllerAdvice(assignableTypes = [V1AccountManagementController::class])
class V1AccountManagementControllerAdvice : BaseExceptionHandler(FeatureCode.V1_ACCOUNT_MANAGEMENT_SERVICE)

@Order(value = 1)
@RestControllerAdvice(assignableTypes = [V1AccountValidationController::class])
class V1AccountValidationControllerAdvice : BaseExceptionHandler(FeatureCode.V1_ACCOUNT_VALIDATION_SERVICE)

@Order(value = 1)
@RestControllerAdvice(assignableTypes = [V1AccountTransactionController::class])
class V1AccountTransactionControllerAdvice : BaseExceptionHandler(FeatureCode.V1_ACCOUNT_TRANSACTION_SERVICE)

@Order(value = 1)
@RestControllerAdvice(assignableTypes = [V1ExchangeRateFindController::class])
class V1ExchangeRateFindControllerAdvice : BaseExceptionHandler(FeatureCode.V1_EXCHANGE_RATE_FIND_SERVICE)