package com.konai.fxs.common.enumerate

enum class ExternalComponent(private val propertyName: String) {

    KNOTIFY("knotify"),
    KOREAEXIM("koreaexim"),
    ;

    fun getPropertyName(): String = this.propertyName

}