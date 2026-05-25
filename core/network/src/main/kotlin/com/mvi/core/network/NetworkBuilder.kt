package com.mvi.core.network

import com.mvi.core.environment.Environment
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Inject

class NetworkBuilder @Inject internal constructor(
    private val environment: Environment,
    private val builder: Retrofit.Builder,
    private val okHttpClient: OkHttpClient,
) {

    private var serviceUrl: ServiceUrl? = null
    private var config: (OkHttpClient.Builder.() -> OkHttpClient) = { build() }

    fun withServiceUrl(serviceUrl: ServiceUrl): NetworkBuilder {
        this.serviceUrl = serviceUrl
        return this
    }

    fun withHttpClientConfig(config: OkHttpClient.Builder.() -> OkHttpClient): NetworkBuilder {
        this.config = config
        return this
    }

    fun build(): Network = Network(
        retrofit = builder
            .client(config.invoke(okHttpClient.newBuilder()))
            .baseUrl(serviceUrl?.value ?: "https://example.org")
            .build(),
        environment = environment,
    )
}
