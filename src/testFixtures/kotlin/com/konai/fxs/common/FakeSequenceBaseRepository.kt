package com.konai.fxs.common

import com.konai.fxs.common.entity.SequenceBaseEntity
import java.security.SecureRandom

open class FakeSequenceBaseRepository<T : SequenceBaseEntity> {

    private val entities = mutableMapOf<Long, T>()

    protected fun save(entity: T): T {
        val id = entity.id ?: SecureRandom().nextLong()
        entities[id] = entity
        return entity.apply { this.id = id }
    }

    protected fun findById(id: Long): T? {
        return entities[id]
    }

}