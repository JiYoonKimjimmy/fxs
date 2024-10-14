package com.konai.fxs.common.enumerate

enum class ComponentName(private val propertyName: String) {

    KNOTIFY("knotify")
    ;

    fun getPropertyName(): String = this.propertyName

}