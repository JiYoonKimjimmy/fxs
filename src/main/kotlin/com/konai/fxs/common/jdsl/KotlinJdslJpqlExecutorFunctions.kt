package com.konai.fxs.common.jdsl

import com.konai.fxs.common.getContentFirstOrNull
import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.JpqlQueryable
import com.linecorp.kotlinjdsl.querymodel.jpql.select.SelectQuery
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.domain.PageRequest

fun <E : Any> KotlinJdslJpqlExecutor.findByPredicate(query: () -> Jpql.() -> JpqlQueryable<SelectQuery<E>>): E? {
    return this.findSlice(
            pageable = PageRequest.of(0, 1),
            init = query()
        )
        .getContentFirstOrNull()
}