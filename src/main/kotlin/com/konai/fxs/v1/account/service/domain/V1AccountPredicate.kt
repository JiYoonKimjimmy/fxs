package com.konai.fxs.v1.account.service.domain

import com.konai.fxs.common.jdsl.ifNotNullEquals
import com.konai.fxs.v1.account.repository.entity.V1AccountEntity
import com.konai.fxs.v1.account.repository.entity.V1AcquirerEntity
import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.JpqlQueryable
import com.linecorp.kotlinjdsl.querymodel.jpql.path.Paths
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicatable
import com.linecorp.kotlinjdsl.querymodel.jpql.select.SelectQuery
import java.math.BigDecimal

class V1AccountPredicate(
    val id: Long? = null,
    val acquirer: V1Acquirer? = null,
    val currency: String? = null,
    val balance: BigDecimal? = null,
    val minRequiredBalance: BigDecimal? = null,
    val averageExchangeRate: BigDecimal? = null
) {

    fun selectQuery(): Jpql.() -> JpqlQueryable<SelectQuery<V1AccountEntity>> {
        return {
            select(entity(V1AccountEntity::class))
                .from(entity(V1AccountEntity::class))
                .whereAnd(*whereQuery())
        }
    }

    private fun whereQuery(): Array<Predicatable?> {
        return arrayOf(
            this.id                 .ifNotNullEquals(Paths.path(V1AccountEntity::id)),
            this.acquirer?.id       .ifNotNullEquals(Paths.path(Paths.path(V1AccountEntity::acquirer), V1AcquirerEntity::id)),
            this.acquirer?.type     .ifNotNullEquals(Paths.path(Paths.path(V1AccountEntity::acquirer), V1AcquirerEntity::type)),
            this.acquirer?.name     .ifNotNullEquals(Paths.path(Paths.path(V1AccountEntity::acquirer), V1AcquirerEntity::name)),
            this.currency           .ifNotNullEquals(Paths.path(V1AccountEntity::currency)),
            this.balance            .ifNotNullEquals(Paths.path(V1AccountEntity::balance)),
            this.minRequiredBalance .ifNotNullEquals(Paths.path(V1AccountEntity::minRequiredBalance)),
            this.averageExchangeRate.ifNotNullEquals(Paths.path(V1AccountEntity::averageExchangeRate)),
        )
    }

}