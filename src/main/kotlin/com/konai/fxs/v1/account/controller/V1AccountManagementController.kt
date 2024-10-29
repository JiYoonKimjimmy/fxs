package com.konai.fxs.v1.account.controller

import com.konai.fxs.v1.account.controller.model.*
import com.konai.fxs.v1.account.service.V1AccountManagementService
import com.konai.fxs.v1.account.service.domain.V1AccountMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/account")
@RestController
class V1AccountManagementController(
    private val v1AccountMapper: V1AccountMapper,
    private val v1AccountManagementService: V1AccountManagementService
) {

    @PostMapping
    fun create(@RequestBody request: V1CreateAccountRequest): ResponseEntity<V1CreateAccountResponse> {
        return v1AccountMapper.requestToDomain(request)
            .let { v1AccountManagementService.save(it) }
            .let { v1AccountMapper.domainToModel(it) }
            .let { V1CreateAccountResponse(it) }
            .success(HttpStatus.CREATED)
    }

    @PostMapping("/one")
    fun findOne(@RequestBody request: V1FindOneAccountRequest): ResponseEntity<V1FindOneAccountResponse> {
        return request.validation()
            .let { v1AccountMapper.requestToPredicate(request) }
            .let { v1AccountManagementService.findByPredicate(it) }
            .let { v1AccountMapper.domainToModel(it) }
            .let { V1FindOneAccountResponse(it) }
            .success(HttpStatus.OK)
    }

    @PostMapping("/update")
    fun update(@RequestBody request: V1UpdateAccountRequest): ResponseEntity<V1UpdateAccountResponse> {
        return request.validation()
            .let { v1AccountMapper.requestToPredicate(it) }
            .let { v1AccountManagementService.update(it) }
            .let { v1AccountMapper.domainToModel(it) }
            .let { V1UpdateAccountResponse(it) }
            .success(HttpStatus.OK)
    }

}