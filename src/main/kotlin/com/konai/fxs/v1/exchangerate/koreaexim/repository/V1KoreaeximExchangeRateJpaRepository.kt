package com.konai.fxs.v1.exchangerate.koreaexim.repository

import com.konai.fxs.v1.exchangerate.koreaexim.repository.entity.V1KoreaeximExchangeRateEntity
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface V1KoreaeximExchangeRateJpaRepository : JpaRepository<V1KoreaeximExchangeRateEntity, Long>, KotlinJdslJpqlExecutor