package com.konai.fxs.common

import com.konai.fxs.common.entity.SequenceBaseEntity
import com.konai.fxs.testsupport.TestExtensionFunctions

open class FakeSequenceBaseRepository<T : SequenceBaseEntity> {

    protected val entities = mutableMapOf<Long, T>()

    protected fun save(entity: T): T {
        val id = entity.id ?: TestExtensionFunctions.generateSequence()
        entities[id] = entity
        return entity.apply { this.id = id }
    }

    protected fun findById(id: Long): T? {
        return entities[id]
    }

}