package com.konai.fxs.testsupport

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.transaction.annotation.Transactional

@TestDatabaseConfiguration
@Transactional
@DataJpaTest
annotation class CustomDataJpaTest