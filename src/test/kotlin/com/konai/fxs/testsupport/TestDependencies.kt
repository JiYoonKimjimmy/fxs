package com.konai.fxs.testsupport

import com.konai.fxs.common.external.koreaexim.FakeKoreaeximHttpServiceProxy
import com.konai.fxs.common.external.koreaexim.KoreaeximHttpServiceImpl
import com.konai.fxs.common.lock.FakeDistributedLockManagerImpl
import com.konai.fxs.common.message.MessageQueuePublisherImpl
import com.konai.fxs.common.retry.FakeRetryableManagerImpl
import com.konai.fxs.infra.config.ApplicationProperties
import com.konai.fxs.testsupport.event.FakeApplicationEventPublisher
import com.konai.fxs.testsupport.rabbitmq.MockRabbitMQ
import com.konai.fxs.testsupport.redis.EmbeddedRedis
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
import com.konai.fxs.v1.exchangerate.koreaexim.repository.FakeV1KoreaeximExchangeRateRepositoryImpl
import com.konai.fxs.v1.exchangerate.koreaexim.repository.entity.V1KoreaeximExchangeRateEntityFixture
import com.konai.fxs.v1.exchangerate.koreaexim.service.V1KoreaeximExchangeRateCollectServiceImpl
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRateFixture
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRateMapper
import com.konai.fxs.v1.sequence.repository.FakeV1SequenceGeneratorRepositoryImpl
import com.konai.fxs.v1.sequence.service.V1SequenceGeneratorServiceImpl
import com.konai.fxs.v1.sequence.service.domain.V1SequenceGeneratorMapper
import com.konai.fxs.v1.transaction.controller.model.*
import com.konai.fxs.v1.transaction.repository.FakeV1TransactionRepositoryImpl
import com.konai.fxs.v1.transaction.repository.cache.V1TransactionCacheRepositoryImpl
import com.konai.fxs.v1.transaction.repository.entity.V1TransactionEntityFixture
import com.konai.fxs.v1.transaction.service.*
import com.konai.fxs.v1.transaction.service.cache.V1TransactionCacheServiceImpl
import com.konai.fxs.v1.transaction.service.domain.V1TransactionFixture
import com.konai.fxs.v1.transaction.service.domain.V1TransactionMapper
import com.konai.fxs.v1.transaction.service.event.V1TransactionEventPublisherImpl
import com.konai.fxs.v1.transaction.service.event.V1TransactionEventServiceImpl

object TestDependencies {

    // ext-library
    val numberRedisTemplate = EmbeddedRedis.numberRedisTemplate
    val rabbitTemplate = MockRabbitMQ.rabbitTemplate
    val messageQueuePublisher = MessageQueuePublisherImpl(rabbitTemplate)

    private val applicationProperties = ApplicationProperties(
        koreaeximApiKey = "M6qkz3m4nxahvH47Ilu4Rx3k91yDdAxh",
        koreaeximApiType = "AP01"
    )

    private val fakeApplicationEventPublisher = FakeApplicationEventPublisher()
    private val fakeDistributedLockManager = FakeDistributedLockManagerImpl()
    private val fakeRetryableManager = FakeRetryableManagerImpl()
    private val fakeKoreaeximHttpServiceProxy = FakeKoreaeximHttpServiceProxy()

    // mapper
    private val v1AccountMapper = V1AccountMapper()
    private val v1SequenceGeneratorMapper = V1SequenceGeneratorMapper()
    val v1TransactionMapper = V1TransactionMapper()
    private val v1KoreaeximExchangeRateMapper = V1KoreaeximExchangeRateMapper()

    // repository
    private val fakeV1AccountRepository = FakeV1AccountRepositoryImpl(v1AccountMapper)
    private val fakeV1TransactionRepository = FakeV1TransactionRepositoryImpl(v1TransactionMapper)
    val fakeV1SequenceGeneratorRepository = FakeV1SequenceGeneratorRepositoryImpl(v1SequenceGeneratorMapper)
    val v1TransactionCacheRepository = V1TransactionCacheRepositoryImpl(numberRedisTemplate)
    val fakeV1KoreaeximExchangeRateRepository = FakeV1KoreaeximExchangeRateRepositoryImpl(v1KoreaeximExchangeRateMapper)

    // service
    val v1TransactionCacheService = V1TransactionCacheServiceImpl(v1TransactionCacheRepository)

    val v1AccountSaveService = V1AccountSaveServiceImpl(fakeV1AccountRepository)
    val v1AccountFindService = V1AccountFindServiceImpl(fakeV1AccountRepository)
    val v1AccountManagementService = V1AccountManagementServiceImpl(v1AccountSaveService, v1AccountFindService)
    val v1AccountValidationService = V1AccountValidationServiceImpl(v1AccountFindService, v1TransactionCacheService)

    val v1SequenceGeneratorService = V1SequenceGeneratorServiceImpl(fakeV1SequenceGeneratorRepository, fakeDistributedLockManager)

    private val v1TransactionEventPublisher = V1TransactionEventPublisherImpl(v1TransactionMapper, fakeApplicationEventPublisher)
    private val v1TransactionSaveService = V1TransactionSaveServiceImpl(fakeV1TransactionRepository, fakeRetryableManager)
    val v1TransactionFindService = V1TransactionFindServiceImpl(fakeV1TransactionRepository)
    val v1TransactionDepositService = V1TransactionDepositServiceImpl(
        v1AccountValidationService,
        v1AccountSaveService,
        v1TransactionEventPublisher,
        fakeDistributedLockManager
    )
    val v1TransactionWithdrawalService = V1TransactionWithdrawalServiceImpl(
        v1AccountValidationService,
        v1AccountSaveService,
        v1TransactionFindService,
        v1TransactionCacheService,
        v1TransactionEventPublisher,
        fakeDistributedLockManager
    )
    val v1AccountTransactionService = V1AccountTransactionServiceImpl(
        v1TransactionDepositService,
        v1TransactionWithdrawalService,
        v1SequenceGeneratorService
    )
    val v1TransactionEventService = V1TransactionEventServiceImpl(
        v1TransactionMapper,
        v1TransactionSaveService,
        v1TransactionEventPublisher,
        v1AccountTransactionService,
        messageQueuePublisher
    )
    private val koreaeximHttpService = KoreaeximHttpServiceImpl(v1KoreaeximExchangeRateMapper, fakeKoreaeximHttpServiceProxy, applicationProperties)
    val v1KoreaeximExchangeRateCollectService = V1KoreaeximExchangeRateCollectServiceImpl(koreaeximHttpService, fakeV1KoreaeximExchangeRateRepository)

    // fixture
    val v1AccountFixture = V1AccountFixture()
    val v1AccountEntityFixture = V1AccountEntityFixture()
    val v1TransactionFixture = V1TransactionFixture()
    val v1TransactionEntityFixture = V1TransactionEntityFixture()
    val v1KoreaeximExchangeRateFixture = V1KoreaeximExchangeRateFixture()

    val v1FindAllAccountRequestFixture = V1FindAllAccountRequestFixture()
    val v1UpdateAccountRequestFixture = V1UpdateAccountRequestFixture()
    val v1TransactionManualDepositRequestFixture = V1TransactionManualDepositRequestFixture()
    val v1TransactionManualWithdrawalRequestFixture = V1TransactionManualWithdrawalRequestFixture()
    val v1TransactionWithdrawalRequestFixture = V1TransactionWithdrawalRequestFixture()
    val v1TransactionWithdrawalCompleteRequestFixture = V1TransactionWithdrawalCompleteRequestFixture()
    val v1TransactionWithdrawalCancelRequestFixture = V1TransactionWithdrawalCancelRequestFixture()
    val v1KoreaeximExchangeRateEntityFixture = V1KoreaeximExchangeRateEntityFixture()

}