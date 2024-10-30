package com.konai.fxs.common.jdsl

import com.konai.fxs.common.emptyStringToNull
import com.konai.fxs.common.firstOrNull
import com.konai.fxs.common.model.PageableRequest
import com.konai.fxs.common.util.PageRequestUtil.toPageRequest
import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.JpqlQueryable
import com.linecorp.kotlinjdsl.querymodel.jpql.expression.Expressionable
import com.linecorp.kotlinjdsl.querymodel.jpql.expression.Expressions
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicatable
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicates
import com.linecorp.kotlinjdsl.querymodel.jpql.select.SelectQuery
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.domain.Page

fun <E : Any> KotlinJdslJpqlExecutor.findOne(query: Jpql.() -> JpqlQueryable<SelectQuery<E>>): E? {
    return this.findSlice(pageable = PageableRequest.SINGLE, init = query).firstOrNull()
}

fun <E : Any> KotlinJdslJpqlExecutor.findAll(pageable: PageableRequest, query: Jpql.() -> JpqlQueryable<SelectQuery<E>>): Page<E?> {
    return this.findPage(pageable = pageable.toPageRequest(), init = query)
}

fun <T : Any> T?.ifNotNullEquals(expression: Expressionable<T>): Predicatable? {
    return when (this) {
        is String -> this.emptyStringToNull()?.let { Predicates.equal(expression.toExpression(), Expressions.value(it)) }
        else -> this?.let { Predicates.equal(expression.toExpression(), Expressions.value(it)) }
    }
}