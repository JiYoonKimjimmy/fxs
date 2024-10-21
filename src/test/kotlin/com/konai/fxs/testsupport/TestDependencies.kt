package com.konai.fxs.testsupport

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.konai.fxs.v1.account.repository.FakeV1AccountRepositoryImpl
import com.konai.fxs.v1.account.repository.entity.V1AccountEntityFixture
import com.konai.fxs.v1.account.service.V1AccountManagementServiceImpl
import com.konai.fxs.v1.account.service.domain.V1AccountMapper

object TestDependencies {

    // mapper
    val v1AccountMapper = V1AccountMapper()

    // repository
    val fakeV1AccountRepository = FakeV1AccountRepositoryImpl(v1AccountMapper)

    // service
    val v1AccountManagementService = V1AccountManagementServiceImpl(fakeV1AccountRepository)

    // fixture
    val v1AccountEntityFixture = V1AccountEntityFixture()

    // etc
    val objectMapper: ObjectMapper = jacksonObjectMapper().registerModule(kotlinModule())

}