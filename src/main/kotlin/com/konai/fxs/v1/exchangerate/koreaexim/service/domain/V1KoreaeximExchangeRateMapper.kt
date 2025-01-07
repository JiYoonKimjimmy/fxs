package com.konai.fxs.v1.exchangerate.koreaexim.service.domain

import com.konai.fxs.common.convertToDouble
import com.konai.fxs.v1.exchangerate.controller.model.V1KoreaeximExchangeRateModel
import com.konai.fxs.v1.exchangerate.koreaexim.repository.entity.V1KoreaeximExchangeRateEntity
import org.springframework.stereotype.Component

@Component
class V1KoreaeximExchangeRateMapper {

    fun domainToEntity(domain: V1KoreaeximExchangeRate): V1KoreaeximExchangeRateEntity {
        return V1KoreaeximExchangeRateEntity(
            index = domain.index!!,
            registerDate = domain.registerDate,
            result = domain.result,
            curUnit = domain.curUnit,
            curNm = domain.curNm,
            ttb = domain.ttb,
            tts = domain.tts,
            dealBasR = domain.dealBasR,
            bkpr = domain.bkpr,
            yyEfeeR = domain.yyEfeeR,
            tenDdEfeeR = domain.tenDdEfeeR,
            kftcDealBasR = domain.kftcDealBasR,
            kftcBkpr = domain.kftcBkpr,
        )
    }

    fun entityToDomain(entity: V1KoreaeximExchangeRateEntity): V1KoreaeximExchangeRate {
        return V1KoreaeximExchangeRate(
            index = entity.index,
            registerDate = entity.registerDate,
            result = entity.result,
            curUnit = entity.curUnit,
            curNm = entity.curNm,
            ttb = entity.ttb,
            tts = entity.tts,
            dealBasR = entity.dealBasR,
            bkpr = entity.bkpr,
            yyEfeeR = entity.yyEfeeR,
            tenDdEfeeR = entity.tenDdEfeeR,
            kftcDealBasR = entity.kftcDealBasR,
            kftcBkpr = entity.kftcBkpr,
        )
    }

    fun domainToModel(domain: V1KoreaeximExchangeRate): V1KoreaeximExchangeRateModel {
        return V1KoreaeximExchangeRateModel(
            currency = domain.curUnit,
            currencyName = domain.curNm,
            ttBuyRate = domain.ttb.convertToDouble(),
            ttSellRate = domain.tts.convertToDouble(),
            dealCriteriaRate = domain.dealBasR.convertToDouble()
        )
    }

}