package com.mvi.core.network

import com.mvi.core.environment.Environment
import retrofit2.Retrofit
import retrofit2.create

/**
 * A factory class that adapts the environment to provide either a mock client for testing
 * or a production client for real API calls.
 *
 * ## Usage
 * ```kotlin
 * val network = networkModule.network()
 * val api = network.create(MyApi::class.java)
 *
 * // For REST APIs
 * val result = api.getData().execute()
 *
 * // For Mock mode
 * val mockResult = api.getData() // Returns MockResponse directly
 * ```
 *
 * ## Threading
 * This class is main-safe. All [create] operations are thread-safe.
 *
 * ## Properties
 * - [retrofit] The Retrofit client for production API calls
 * - [environment] The current environment (mock/production)
 *
 * ## Example
 * ```kotlin
 * // Production
 * val productionNetwork = Network(retrofit = productionRetrofit, environment = Environment.PRODUCTION)
 * val productionApi = productionNetwork.create(Api::class.java)
 *
 * // Mock (for testing)
 * val mockNetwork = Network(retrofit = mockRetrofit, environment = Environment.MOCK)
 * val mockApi = mockNetwork.create(Api::class.java)
 * ```
 *
 * @property retrofit The Retrofit client for production API calls
 * @property environment The current environment (mock/production)
 *
 * @see Retrofit
 * @see Environment
 */
class Network internal constructor(
    val retrofit: Retrofit,
    val environment: Environment,
) {

    /**
     * Creates a reified type instance based on the environment.
     *
     * In MOCK environment, returns the mock implementation; otherwise creates the actual service.
     *
     * @param factory A lambda that returns the reified type to create
     * @return An instance of the reified type (mock or production)
     *
     * ## Example
     * ```kotlin
     * val api: UserApi = network.create { mockRetrofit.create() }
     *
     * val service = network.create { MyService() }
     * val response = network.create { MyRetrofit.create() }
     * ```
     */
    inline fun <reified T> create(factory: () -> T): T =
        when {
            environment.isMock -> factory()
            else -> retrofit.create()
        }
}
