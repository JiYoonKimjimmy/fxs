package com.konai.fxs.v1.exchangerate.koreaexim.repository.jdsl

import com.konai.fxs.common.jdsl.ifNotNullEquals
import com.konai.fxs.v1.exchangerate.koreaexim.repository.entity.V1KoreaeximExchangeRateEntity
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRatePredicate
import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.JpqlQueryable
import com.linecorp.kotlinjdsl.querymodel.jpql.path.Paths
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicatable
import com.linecorp.kotlinjdsl.querymodel.jpql.select.SelectQuery

class V1KoreaeximExchangeRateJdslExecutor(
    private val predicate: V1KoreaeximExchangeRatePredicate
) {

    fun selectQuery(): Jpql.() -> JpqlQueryable<SelectQuery<V1KoreaeximExchangeRateEntity>> {
        return {
            select(entity(V1KoreaeximExchangeRateEntity::class))
                .from(entity(V1KoreaeximExchangeRateEntity::class))
                .whereAnd(*whereQuery())
        }
    }

    private fun whereQuery(): Array<Predicatable?> {
        return arrayOf(
            predicate.id          .ifNotNullEquals(Paths.path(V1KoreaeximExchangeRateEntity::id)),
            predicate.registerDate.ifNotNullEquals(Paths.path(V1KoreaeximExchangeRateEntity::registerDate)),
            predicate.index       .ifNotNullEquals(Paths.path(V1KoreaeximExchangeRateEntity::index)),
            predicate.result      .ifNotNullEquals(Paths.path(V1KoreaeximExchangeRateEntity::result)),
            predicate.curUnit     .ifNotNullEquals(Paths.path(V1KoreaeximExchangeRateEntity::curUnit)),
            predicate.curNm       .ifNotNullEquals(Paths.path(V1KoreaeximExchangeRateEntity::curNm)),
            predicate.ttb         .ifNotNullEquals(Paths.path(V1KoreaeximExchangeRateEntity::ttb)),
            predicate.tts         .ifNotNullEquals(Paths.path(V1KoreaeximExchangeRateEntity::tts)),
            predicate.dealBasR    .ifNotNullEquals(Paths.path(V1KoreaeximExchangeRateEntity::dealBasR)),
            predicate.bkpr        .ifNotNullEquals(Paths.path(V1KoreaeximExchangeRateEntity::bkpr)),
            predicate.yyEfeeR     .ifNotNullEquals(Paths.path(V1KoreaeximExchangeRateEntity::yyEfeeR)),
            predicate.tenDdEfeeR  .ifNotNullEquals(Paths.path(V1KoreaeximExchangeRateEntity::tenDdEfeeR)),
            predicate.kftcDealBasR.ifNotNullEquals(Paths.path(V1KoreaeximExchangeRateEntity::kftcDealBasR)),
            predicate.kftcBkpr    .ifNotNullEquals(Paths.path(V1KoreaeximExchangeRateEntity::kftcBkpr)),
        )
    }

}