package com.konai.fxs.testsupport

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.konai.fxs.v1.account.controller.model.V1FindAllAccountRequestFixture
import com.konai.fxs.v1.account.controller.model.V1UpdateAccountRequestFixture
import com.konai.fxs.v1.account.repository.FakeV1AccountRepositoryImpl
import com.konai.fxs.v1.account.repository.entity.V1AccountEntityFixture
import com.konai.fxs.v1.account.service.V1AccountFindServiceImpl
import com.konai.fxs.v1.account.service.V1AccountManagementServiceImpl
import com.konai.fxs.v1.account.service.V1AccountSaveServiceImpl
import com.konai.fxs.v1.account.service.domain.V1AccountFixture
import com.konai.fxs.v1.account.service.domain.V1AccountMapper

object TestDependencies {

    // mapper
    private val v1AccountMapper = V1AccountMapper()

    // repository
    private val fakeV1AccountRepository = FakeV1AccountRepositoryImpl(v1AccountMapper)

    // service
    private val v1AccountSaveService = V1AccountSaveServiceImpl(fakeV1AccountRepository)
    private val v1AccountFindService = V1AccountFindServiceImpl(fakeV1AccountRepository)
    val v1AccountManagementService = V1AccountManagementServiceImpl(v1AccountSaveService, v1AccountFindService)

    // fixture
    val v1AccountFixture = V1AccountFixture()
    val v1AccountEntityFixture = V1AccountEntityFixture()

    val v1FindAllAccountRequestFixture = V1FindAllAccountRequestFixture()
    val v1UpdateAccountRequestFixture = V1UpdateAccountRequestFixture()

    // etc
    val objectMapper: ObjectMapper = jacksonObjectMapper().registerModule(kotlinModule())

}