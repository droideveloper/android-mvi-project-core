package com.mvi.core.network

import retrofit2.Retrofit
import retrofit2.create

class Network internal constructor(
    val retrofit: Retrofit? = null,
) {

    inline fun <reified T> create(factory: () -> T): T =
        retrofit?.create() ?: factory()
}
