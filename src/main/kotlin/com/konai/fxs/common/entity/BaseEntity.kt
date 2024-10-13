package com.konai.fxs.common.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
abstract class BaseEntity {

    @CreatedDate
    @Column(name = "SYS_CRE_DTTM", nullable = false, updatable = false)
    var createdDate: LocalDateTime? = null

    @LastModifiedDate
    @Column(name = "SYS_UPD_DTTM", nullable = true)
    var lastModifiedDate: LocalDateTime? = null

    @CreatedBy
    @Column(name = "SYS_CRE_ID", nullable = false, updatable = false, length = 30)
    var createdBy: String? = null

    @LastModifiedBy
    @Column(name = "SYS_UPD_ID", nullable = true, length = 30)
    var lastModifiedBy: String? = null

    @PrePersist
    fun onPrePersist() {
        this.lastModifiedDate = null
        this.lastModifiedBy = null
    }

    @PreUpdate
    fun onPreUpdate() {
        this.lastModifiedDate = LocalDateTime.now()
    }

}