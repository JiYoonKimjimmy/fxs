package com.konai.fxs.common.enumerate

enum class ExternalComponent(private val propertyName: String) {

    KNOTIFY("knotify")
    ;

    fun getPropertyName(): String = this.propertyName

}