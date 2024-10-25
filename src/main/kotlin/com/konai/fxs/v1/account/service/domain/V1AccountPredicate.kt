package com.konai.fxs.v1.account.service.domain

import com.konai.fxs.v1.account.repository.entity.V1AccountEntity
import com.konai.fxs.v1.account.repository.entity.V1AcquirerEntity
import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.JpqlQueryable
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
    private val acquirerEntity: V1AcquirerEntity? = acquirer?.let { V1AcquirerEntity(id = it.id, type = it.type, name = it.name) }

    fun generateQuery(): Jpql.() -> JpqlQueryable<SelectQuery<V1AccountEntity>> {
        val query: (Jpql.() -> JpqlQueryable<SelectQuery<V1AccountEntity>>) = {
            select(entity(V1AccountEntity::class))
                .from(entity(V1AccountEntity::class))
                .whereAnd(
                    this@V1AccountPredicate.id                  ?.let { path(V1AccountEntity::id).eq(it) },
                    this@V1AccountPredicate.acquirerEntity      ?.let { path(V1AccountEntity::acquirer).eq(it) },
                    this@V1AccountPredicate.currency            ?.let { path(V1AccountEntity::currency).eq(it) },
                    this@V1AccountPredicate.balance             ?.let { path(V1AccountEntity::balance).eq(it) },
                    this@V1AccountPredicate.minRequiredBalance  ?.let { path(V1AccountEntity::minRequiredBalance).eq(it) },
                    this@V1AccountPredicate.averageExchangeRate ?.let { path(V1AccountEntity::averageExchangeRate).eq(it) },
                )
        }
        return query
    }

}