package com.konai.fxs.v1.transaction.repository.jdsl

import com.konai.fxs.common.jdsl.ifNotNullEquals
import com.konai.fxs.v1.account.repository.entity.V1AccountEntity.V1AcquirerEntity
import com.konai.fxs.v1.transaction.repository.entity.V1TransactionEntity
import com.konai.fxs.v1.transaction.service.domain.V1TransactionPredicate
import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.JpqlQueryable
import com.linecorp.kotlinjdsl.querymodel.jpql.path.Paths
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicatable
import com.linecorp.kotlinjdsl.querymodel.jpql.select.SelectQuery

class V1TransactionJdslExecutor(
    private val predicate: V1TransactionPredicate
) {

    fun selectQuery(): Jpql.() -> JpqlQueryable<SelectQuery<V1TransactionEntity>> {
        return {
            select(entity(V1TransactionEntity::class))
                .from(entity(V1TransactionEntity::class))
                .whereAnd(*whereQuery())
        }
    }

    private fun whereQuery(): Array<Predicatable?> {
        return arrayOf(
            predicate.id                 .ifNotNullEquals(Paths.path(V1TransactionEntity::id)),
            predicate.acquirer?.id       .ifNotNullEquals(Paths.path(Paths.path(V1TransactionEntity::acquirer), V1AcquirerEntity::id)),
            predicate.acquirer?.type     .ifNotNullEquals(Paths.path(Paths.path(V1TransactionEntity::acquirer), V1AcquirerEntity::type)),
            predicate.acquirer?.name     .ifNotNullEquals(Paths.path(Paths.path(V1TransactionEntity::acquirer), V1AcquirerEntity::name)),
            predicate.fromAcquirer?.id   .ifNotNullEquals(Paths.path(Paths.path(V1TransactionEntity::fromAcquirer), V1AcquirerEntity::id)),
            predicate.fromAcquirer?.type .ifNotNullEquals(Paths.path(Paths.path(V1TransactionEntity::fromAcquirer), V1AcquirerEntity::type)),
            predicate.fromAcquirer?.name .ifNotNullEquals(Paths.path(Paths.path(V1TransactionEntity::fromAcquirer), V1AcquirerEntity::name)),
            predicate.type               .ifNotNullEquals(Paths.path(V1TransactionEntity::type)),
            predicate.purpose            .ifNotNullEquals(Paths.path(V1TransactionEntity::purpose)),
            predicate.channel            .ifNotNullEquals(Paths.path(V1TransactionEntity::channel)),
            predicate.currency           .ifNotNullEquals(Paths.path(V1TransactionEntity::currency)),
            predicate.amount             .ifNotNullEquals(Paths.path(V1TransactionEntity::amount)),
            predicate.exchangeRate       .ifNotNullEquals(Paths.path(V1TransactionEntity::exchangeRate)),
            predicate.transferDate       .ifNotNullEquals(Paths.path(V1TransactionEntity::transferDate)),
            predicate.requestBy          .ifNotNullEquals(Paths.path(V1TransactionEntity::requestBy)),
            predicate.status             .ifNotNullEquals(Paths.path(V1TransactionEntity::status)),
        )
    }

}