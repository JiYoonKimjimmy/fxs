package com.konai.fxs.common.restclient

enum class ComponentName(private val propertyName: String) {

    KNOTIFY("knotify")
    ;

    fun getPropertyName(): String = this.propertyName

}