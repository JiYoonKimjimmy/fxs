package com.konai.fxs.v1.exchangerate.koreaexim.repository

import com.konai.fxs.common.jdsl.findAll
import com.konai.fxs.common.jdsl.findOne
import com.konai.fxs.common.model.BasePageable
import com.konai.fxs.common.model.PageableRequest
import com.konai.fxs.common.util.PageRequestUtil.toBasePageable
import com.konai.fxs.v1.exchangerate.koreaexim.repository.jdsl.V1KoreaeximExchangeRateJdslExecutor
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRate
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRateMapper
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRatePredicate
import org.springframework.stereotype.Repository

@Repository
class V1KoreaeximExchangeRateRepositoryImpl(
    private val v1KoreaeximExchangeRateMapper: V1KoreaeximExchangeRateMapper,
    private val v1KoreaeximExchangeRateJpaRepository: V1KoreaeximExchangeRateJpaRepository
) : V1KoreaeximExchangeRateRepository {

    override fun saveAll(exchangeRates: List<V1KoreaeximExchangeRate>): List<V1KoreaeximExchangeRate> {
        return exchangeRates
            .map { v1KoreaeximExchangeRateMapper.domainToEntity(it) }
            .let { v1KoreaeximExchangeRateJpaRepository.saveAll(it) }
            .map { v1KoreaeximExchangeRateMapper.entityToDomain(it) }
    }

    override fun findByPredicate(predicate: V1KoreaeximExchangeRatePredicate): V1KoreaeximExchangeRate? {
        return V1KoreaeximExchangeRateJdslExecutor(predicate)
            .let { v1KoreaeximExchangeRateJpaRepository.findOne(it.selectQuery()) }
            ?.let { v1KoreaeximExchangeRateMapper.entityToDomain(it) }
    }

    override fun findAllByPredicate(predicate: V1KoreaeximExchangeRatePredicate, pageable: PageableRequest): BasePageable<V1KoreaeximExchangeRate> {
        return V1KoreaeximExchangeRateJdslExecutor(predicate)
            .let { v1KoreaeximExchangeRateJpaRepository.findAll(pageable, it.selectQuery()) }
            .toBasePageable(v1KoreaeximExchangeRateMapper::entityToDomain)
    }

    override fun findLatestKoreaeximExchangeRate(curUnit: String, registerDate: String): V1KoreaeximExchangeRate? {
        return findAllByPredicate(
                predicate = V1KoreaeximExchangeRatePredicate(registerDate = registerDate, curUnit = curUnit),
                pageable = PageableRequest(number = 0, size = 1, sortBy = "index")
            )
            .content.firstOrNull()
    }

}