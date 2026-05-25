package com.mvi.core.network

import com.mvi.core.environment.Environment
import retrofit2.Retrofit
import retrofit2.create

class Network internal constructor(
    val retrofit: Retrofit,
    val environment: Environment,
) {

    inline fun <reified T> create(factory: () -> T): T =
        when {
            environment.isMock -> factory()
            else -> retrofit.create()
        }
}
