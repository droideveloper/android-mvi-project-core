package com.mvi.core.network

import com.mvi.core.environment.Environment
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * Builder class for configuring and constructing [Network] instances.
 *
 * This builder provides a fluent API for setting up the Retrofit client,
 * configuring service URLs, and customizing the HTTP client with interceptors
 * and retry policies. It adapts the environment to provide either mock or
 * production clients.
 *
 * ## Usage
 * ```kotlin
 * val network = NetworkBuilder(environment, builder, httpClient)
 *     .withServiceUrl("https://api.example.com")
 *     .withHttpClientConfig {
 *         it.addInterceptor(loggingInterceptor())
 *     }
 *     .build()
 * ```
 *
 * ## Threading
 * All configuration methods and [build()] are main-safe.
 *
 * @property environment The current environment (mock/production)
 * @property builder The Retrofit builder with converter factories
 * @property okHttpClient The base OkHttpClient to build upon
 *
 * @see ServiceUrl
 * @see Network
 */
class NetworkBuilder @Inject internal constructor(
    private val environment: Environment,
    private val builder: Retrofit.Builder,
    private val okHttpClient: OkHttpClient,
) {

    /** The service URL for API calls, set via [withServiceUrl]. Defaults to example.org. */
    private var serviceUrl: ServiceUrl? = null

    /** HTTP client configuration lambda to apply after the base OkHttpClient. */
    private var config: (OkHttpClient.Builder.() -> OkHttpClient) = { build() }

    /**
     * Configures the builder to use the provided service URL.
     *
     * The [ServiceUrl] validates that the URL is a valid HTTP/HTTPS address.
     *
     * @param serviceUrl The service URL for the API endpoint
     * @return This builder for method chaining
     */
    fun withServiceUrl(serviceUrl: ServiceUrl): NetworkBuilder {
        this.serviceUrl = serviceUrl
        return this
    }

    /**
     * Configures additional HTTP client behavior via a builder lambda.
     *
     * This is invoked after the base [OkHttpClient] is created and can be
     * used to add interceptors, timeouts, connection pools, etc.
     *
     * @param config A lambda that receives an OkHttpClient.Builder and returns
     *               the configured client
     * @return This builder for method chaining
     */
    fun withHttpClientConfig(config: OkHttpClient.Builder.() -> OkHttpClient): NetworkBuilder {
        this.config = config
        return this
    }

    /**
     * Constructs and returns a configured [Network] instance.
     *
     * Uses the service URL from [withServiceUrl], defaulting to "https://example.org"
     * if [withServiceUrl] was never called. Applies the HTTP client configuration
     * from [withHttpClientConfig].
     *
     * @return A fully configured [Network] instance
     */
    fun build(): Network = Network(
        retrofit = builder
            .client(config.invoke(okHttpClient.newBuilder()))
            .baseUrl(serviceUrl?.value ?: "https://example.org")
            .build(),
        environment = environment,
    )
}
