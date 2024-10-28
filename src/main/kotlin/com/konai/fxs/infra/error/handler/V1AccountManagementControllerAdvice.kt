package com.konai.fxs.infra.error.handler

import com.konai.fxs.infra.error.FeatureCode
import com.konai.fxs.v1.account.controller.V1AccountManagementController
import org.springframework.core.annotation.Order
import org.springframework.web.bind.annotation.RestControllerAdvice

@Order(value = 1)
@RestControllerAdvice(assignableTypes = [V1AccountManagementController::class])
class V1AccountManagementControllerAdvice : BaseExceptionHandler(FeatureCode.V1_ACCOUNT_MANAGEMENT_SERVICE)