package com.konai.fxs.v1.sequence.repository

import com.konai.fxs.common.DEFAULT_SEQUENCE_DATE
import com.konai.fxs.common.enumerate.SequenceType
import com.konai.fxs.v1.sequence.service.domain.V1SequenceGenerator
import com.konai.fxs.v1.sequence.service.domain.V1SequenceGeneratorMapper
import org.springframework.stereotype.Repository

@Repository
class V1SequenceGeneratorRepositoryImpl(
    private val v1SequenceGeneratorMapper: V1SequenceGeneratorMapper,
    private val v1SequenceGeneratorJpaRepository: V1SequenceGeneratorJpaRepository
) : V1SequenceGeneratorRepository {

    override fun next(type: SequenceType): V1SequenceGenerator {
        return findByType(type).increment().let(this::save)
    }

    private fun save(sequenceGenerator: V1SequenceGenerator): V1SequenceGenerator {
        return v1SequenceGeneratorMapper.domainToEntity(sequenceGenerator)
            .let { v1SequenceGeneratorJpaRepository.save(it) }
            .let { v1SequenceGeneratorMapper.entityToDomain(it) }
    }

    private fun findByType(type: SequenceType): V1SequenceGenerator {
        return v1SequenceGeneratorJpaRepository.findByTypeAndDate(type, DEFAULT_SEQUENCE_DATE)
            ?.let { v1SequenceGeneratorMapper.entityToDomain(it) }
            ?: V1SequenceGenerator(type = type, date = DEFAULT_SEQUENCE_DATE, value = 0)
    }

}