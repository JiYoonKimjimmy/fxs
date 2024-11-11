package com.konai.fxs.testsupport

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.konai.fxs.testsupport.redis.RedisTestConfig
import com.konai.fxs.v1.account.controller.model.V1FindAllAccountRequestFixture
import com.konai.fxs.v1.account.controller.model.V1UpdateAccountRequestFixture
import com.konai.fxs.v1.account.repository.FakeV1AccountRepositoryImpl
import com.konai.fxs.v1.account.repository.entity.V1AccountEntityFixture
import com.konai.fxs.v1.account.service.V1AccountFindServiceImpl
import com.konai.fxs.v1.account.service.V1AccountManagementServiceImpl
import com.konai.fxs.v1.account.service.V1AccountSaveServiceImpl
import com.konai.fxs.v1.account.service.V1AccountValidationServiceImpl
import com.konai.fxs.v1.account.service.domain.V1AccountFixture
import com.konai.fxs.v1.account.service.domain.V1AccountMapper
import com.konai.fxs.v1.transaction.repository.cache.TransactionCacheRepositoryImpl
import com.konai.fxs.v1.transaction.service.cache.TransactionCacheServiceImpl

object TestDependencies {

    // ext-library
    val numberRedisTemplate = RedisTestConfig.numberRedisTemplate

    // mapper
    private val v1AccountMapper = V1AccountMapper()

    // repository
    private val fakeV1AccountRepository = FakeV1AccountRepositoryImpl(v1AccountMapper)
    val transactionCacheRepository = TransactionCacheRepositoryImpl(numberRedisTemplate)

    // service
    val transactionCacheService = TransactionCacheServiceImpl(transactionCacheRepository)
    val v1AccountSaveService = V1AccountSaveServiceImpl(fakeV1AccountRepository)
    private val v1AccountFindService = V1AccountFindServiceImpl(fakeV1AccountRepository)
    val v1AccountManagementService = V1AccountManagementServiceImpl(v1AccountSaveService, v1AccountFindService)
    val v1AccountValidationService = V1AccountValidationServiceImpl(v1AccountFindService, transactionCacheService)

    // fixture
    val v1AccountFixture = V1AccountFixture()
    val v1AccountEntityFixture = V1AccountEntityFixture()

    val v1FindAllAccountRequestFixture = V1FindAllAccountRequestFixture()
    val v1UpdateAccountRequestFixture = V1UpdateAccountRequestFixture()

    // etc
    val objectMapper: ObjectMapper = jacksonObjectMapper().registerModule(kotlinModule())

}