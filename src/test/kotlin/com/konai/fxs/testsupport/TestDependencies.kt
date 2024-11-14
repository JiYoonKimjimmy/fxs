package com.konai.fxs.testsupport

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.konai.fxs.common.lock.FakeDistributedLockManagerImpl
import com.konai.fxs.testsupport.event.TestV1TransactionEventHandler
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
import com.konai.fxs.v1.transaction.repository.FakeV1TransactionRepositoryImpl
import com.konai.fxs.v1.transaction.repository.cache.TransactionCacheRepositoryImpl
import com.konai.fxs.v1.transaction.repository.entity.V1TransactionEntityFixture
import com.konai.fxs.v1.transaction.service.V1TransactionDepositServiceImpl
import com.konai.fxs.v1.transaction.service.cache.TransactionCacheServiceImpl
import com.konai.fxs.v1.transaction.service.domain.V1TransactionFixture
import com.konai.fxs.v1.transaction.service.domain.V1TransactionMapper
import com.konai.fxs.v1.transaction.service.event.V1TransactionEventPublisherImpl

object TestDependencies {

    // ext-library
    val numberRedisTemplate = RedisTestConfig.numberRedisTemplate
    private val distributedLockManager = FakeDistributedLockManagerImpl()

    // mapper
    private val v1AccountMapper = V1AccountMapper()
    val v1TransactionMapper = V1TransactionMapper()

    // repository
    val fakeV1AccountRepository = FakeV1AccountRepositoryImpl(v1AccountMapper)
    val fakeV1TransactionRepository = FakeV1TransactionRepositoryImpl(v1TransactionMapper)
    val transactionCacheRepository = TransactionCacheRepositoryImpl(numberRedisTemplate)

    // event handler
    private val v1TransactionEventHandler = TestV1TransactionEventHandler(v1TransactionMapper, fakeV1TransactionRepository)

    // service
    val transactionCacheService = TransactionCacheServiceImpl(transactionCacheRepository)
    val v1AccountSaveService = V1AccountSaveServiceImpl(fakeV1AccountRepository)
    private val v1AccountFindService = V1AccountFindServiceImpl(fakeV1AccountRepository)
    val v1AccountManagementService = V1AccountManagementServiceImpl(v1AccountSaveService, v1AccountFindService)
    val v1AccountValidationService = V1AccountValidationServiceImpl(v1AccountFindService, transactionCacheService)
    private val v1TransactionEventPublisher = V1TransactionEventPublisherImpl(v1TransactionMapper, v1TransactionEventHandler)
    val v1TransactionDepositService = V1TransactionDepositServiceImpl(v1AccountValidationService, v1AccountSaveService, v1TransactionEventPublisher, distributedLockManager)

    // fixture
    val v1AccountFixture = V1AccountFixture()
    val v1AccountEntityFixture = V1AccountEntityFixture()
    val v1TransactionFixture = V1TransactionFixture()
    val v1TransactionEntityFixture = V1TransactionEntityFixture()

    val v1FindAllAccountRequestFixture = V1FindAllAccountRequestFixture()
    val v1UpdateAccountRequestFixture = V1UpdateAccountRequestFixture()

    // etc
    val objectMapper: ObjectMapper = jacksonObjectMapper().registerModule(kotlinModule())

}