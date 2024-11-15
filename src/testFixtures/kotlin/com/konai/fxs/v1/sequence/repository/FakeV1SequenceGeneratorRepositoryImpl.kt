package com.konai.fxs.v1.sequence.repository

import com.konai.fxs.common.DEFAULT_SEQUENCE_DATE
import com.konai.fxs.common.FakeSequenceBaseRepository
import com.konai.fxs.common.enumerate.SequenceType
import com.konai.fxs.v1.sequence.repository.entity.V1SequenceGeneratorEntity
import com.konai.fxs.v1.sequence.service.domain.V1SequenceGenerator
import com.konai.fxs.v1.sequence.service.domain.V1SequenceGeneratorMapper

class FakeV1SequenceGeneratorRepositoryImpl(
    private val v1SequenceGeneratorMapper: V1SequenceGeneratorMapper
) : V1SequenceGeneratorRepository, FakeSequenceBaseRepository<V1SequenceGeneratorEntity>() {

    override fun next(type: SequenceType): V1SequenceGenerator {
        return findByType(type).increment().let(this::save)
    }

    private fun save(sequenceGenerator: V1SequenceGenerator): V1SequenceGenerator {
        return v1SequenceGeneratorMapper.domainToEntity(sequenceGenerator)
            .let { super.save(it) }
            .let { v1SequenceGeneratorMapper.entityToDomain(it) }
    }

    private fun findByType(type: SequenceType): V1SequenceGenerator {
        return super.entities.values.find { it.type == type }
            ?.let { v1SequenceGeneratorMapper.entityToDomain(it) }
            ?: V1SequenceGenerator(type = type, date = DEFAULT_SEQUENCE_DATE, value = 0)
    }

    fun deleteAll() {
        super.entities.clear()
    }

}