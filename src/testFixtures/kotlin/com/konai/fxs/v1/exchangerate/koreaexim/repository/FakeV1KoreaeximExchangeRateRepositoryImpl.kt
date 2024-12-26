package com.konai.fxs.v1.exchangerate.koreaexim.repository

import com.konai.fxs.common.ifNotNullEquals
import com.konai.fxs.common.model.BasePageable
import com.konai.fxs.common.model.PageableRequest
import com.konai.fxs.testsupport.FakeSequenceBaseRepository
import com.konai.fxs.v1.exchangerate.koreaexim.repository.entity.V1KoreaeximExchangeRateEntity
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRate
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRateMapper
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRatePredicate

class FakeV1KoreaeximExchangeRateRepositoryImpl(
    private val v1KoreaeximExchangeRateMapper: V1KoreaeximExchangeRateMapper
) : V1KoreaeximExchangeRateRepository, FakeSequenceBaseRepository<V1KoreaeximExchangeRateEntity>() {

    override fun saveAll(exchangeRates: List<V1KoreaeximExchangeRate>): List<V1KoreaeximExchangeRate> {
        return exchangeRates
            .mapIndexed { i, it -> it.copy(index = i) }
            .map { v1KoreaeximExchangeRateMapper.domainToEntity(it) }
            .map { super.save(it) }
            .map { v1KoreaeximExchangeRateMapper.entityToDomain(it) }
    }

    override fun findByPredicate(predicate: V1KoreaeximExchangeRatePredicate): V1KoreaeximExchangeRate? {
        return super.entities.values.find { checkPredicate(predicate, it) }
            ?.let { v1KoreaeximExchangeRateMapper.entityToDomain(it) }
    }

    override fun findAllByPredicate(predicate: V1KoreaeximExchangeRatePredicate, pageable: PageableRequest): BasePageable<V1KoreaeximExchangeRate> {
        val (totalSize, content) = super.findPage(pageable) { checkPredicate(predicate, it) }
        return BasePageable(
            pageable = BasePageable.Pageable(
                numberOfElements = content.size,
                totalElements = totalSize.toLong(),
            ),
            content = content.map(v1KoreaeximExchangeRateMapper::entityToDomain)
        )
    }

    override fun findLatestKoreaeximExchangeRate(curUnit: String, registerDate: String): V1KoreaeximExchangeRate? {
        return findAllByPredicate(
                predicate = V1KoreaeximExchangeRatePredicate(registerDate = registerDate, curUnit = curUnit),
                pageable = PageableRequest(number = 0, size = 1, sortBy = "index")
            )
            .content
            .firstOrNull()
    }

    private fun checkPredicate(predicate: V1KoreaeximExchangeRatePredicate, entity: V1KoreaeximExchangeRateEntity): Boolean {
        return predicate.id              .ifNotNullEquals(entity.id)
                && predicate.registerDate.ifNotNullEquals(entity.registerDate)
                && predicate.index       .ifNotNullEquals(entity.index)
                && predicate.result      .ifNotNullEquals(entity.result)
                && predicate.curUnit     .ifNotNullEquals(entity.curUnit)
                && predicate.curNm       .ifNotNullEquals(entity.curNm)
                && predicate.ttb         .ifNotNullEquals(entity.ttb)
                && predicate.tts         .ifNotNullEquals(entity.tts)
                && predicate.dealBasR    .ifNotNullEquals(entity.dealBasR)
                && predicate.bkpr        .ifNotNullEquals(entity.bkpr)
                && predicate.yyEfeeR     .ifNotNullEquals(entity.yyEfeeR)
                && predicate.tenDdEfeeR  .ifNotNullEquals(entity.tenDdEfeeR)
                && predicate.kftcDealBasR.ifNotNullEquals(entity.kftcDealBasR)
                && predicate.kftcBkpr    .ifNotNullEquals(entity.kftcBkpr)
    }

}