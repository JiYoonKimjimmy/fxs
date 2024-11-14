package com.konai.fxs.v1.sequence.service.domain

import com.konai.fxs.v1.sequence.repository.entity.V1SequenceGeneratorEntity
import org.springframework.stereotype.Component

@Component
class V1SequenceGeneratorMapper {

    fun domainToEntity(domain: V1SequenceGenerator): V1SequenceGeneratorEntity {
        return V1SequenceGeneratorEntity(
            id = domain.id,
            type = domain.type,
            date = domain.date,
            value = domain.value
        )
    }

    fun entityToDomain(entity: V1SequenceGeneratorEntity): V1SequenceGenerator {
        return V1SequenceGenerator(
            id = entity.id,
            type = entity.type,
            date = entity.date,
            value = entity.value
        )
    }

}