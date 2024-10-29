package com.konai.fxs.v1.account.repository.jdsl

import com.konai.fxs.common.jdsl.ifNotNullEquals
import com.konai.fxs.v1.account.repository.entity.V1AccountEntity
import com.konai.fxs.v1.account.repository.entity.V1AccountEntity.V1AcquirerEntity
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate
import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.JpqlQueryable
import com.linecorp.kotlinjdsl.querymodel.jpql.path.Paths
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicatable
import com.linecorp.kotlinjdsl.querymodel.jpql.select.SelectQuery

class V1AccountJdslPredicate(
    private val predicate: V1AccountPredicate
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
            predicate.id                 .ifNotNullEquals(Paths.path(V1AccountEntity::id)),
            predicate.acquirer?.id       .ifNotNullEquals(Paths.path(Paths.path(V1AccountEntity::acquirer), V1AcquirerEntity::id)),
            predicate.acquirer?.type     .ifNotNullEquals(Paths.path(Paths.path(V1AccountEntity::acquirer), V1AcquirerEntity::type)),
            predicate.acquirer?.name     .ifNotNullEquals(Paths.path(Paths.path(V1AccountEntity::acquirer), V1AcquirerEntity::name)),
            predicate.currency           .ifNotNullEquals(Paths.path(V1AccountEntity::currency)),
            predicate.balance            .ifNotNullEquals(Paths.path(V1AccountEntity::balance)),
            predicate.minRequiredBalance .ifNotNullEquals(Paths.path(V1AccountEntity::minRequiredBalance)),
            predicate.averageExchangeRate.ifNotNullEquals(Paths.path(V1AccountEntity::averageExchangeRate)),
        )
    }

}