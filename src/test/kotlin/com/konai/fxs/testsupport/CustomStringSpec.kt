package com.konai.fxs.testsupport

import io.kotest.core.spec.style.StringSpec

abstract class CustomStringSpec(body: BaseStringSpec.() -> Unit = {}) : BaseStringSpec() {
    init {
        body()
    }
}

abstract class BaseStringSpec(
    val dependencies: TestDependencies = TestDependencies
) : StringSpec()