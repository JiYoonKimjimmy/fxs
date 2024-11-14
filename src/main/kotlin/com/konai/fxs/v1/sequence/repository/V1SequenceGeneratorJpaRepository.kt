package com.konai.fxs.v1.sequence.repository

import com.konai.fxs.common.enumerate.SequenceType
import com.konai.fxs.v1.sequence.repository.entity.V1SequenceGeneratorEntity
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface V1SequenceGeneratorJpaRepository : JpaRepository<V1SequenceGeneratorEntity, Long>, KotlinJdslJpqlExecutor {

    fun findByTypeAndDate(type: SequenceType, date: String): V1SequenceGeneratorEntity?

}