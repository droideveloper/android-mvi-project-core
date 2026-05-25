package com.mvi.core.network.adapter

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Retrofit [CallAdapter.Factory] that intercepts [Call] responses and wraps them in [Result].
 *
 * This adapter handles all retrofit calls uniformly:
 * - Successful responses are wrapped in [Result.success]
 * - HTTP errors are wrapped in [Result.failure]
 * - Network failures are wrapped in [Result.failure]
 * - Registered [FailureHandler] callbacks are invoked on all failures
 *
 * When a custom converter for [Result] is registered, delegates directly to it.
 *
 * ### Example
 * ```kotlin
 * val retrofit = Retrofit.Builder()
 *     .baseUrl("https://api.example.com/")
 *     .addCallAdapterFactory(SuspendResultCallAdapterFactory())
 *     .build()
 *
 * val api = retrofit.create(MyApi::class.java)
 * val response: Call<Result<User>> = api.getUser("123")
 * ```
 *
 * ### Threading
 * All operations follow Retrofit's default threading model:
 * - [enqueue] callbacks run on the network dispatcher thread
 * - [execute] throws [UnsupportedOperationException] if used on main thread
 *
 * @param failureHandler Optional callback for failure reporting. Invoked for both
 *   HTTP errors from [Response.isSuccessful] checks and network exceptions from
 *   the underlying transport layer.
 *
 * @see CatchingCallAdapter
 * @see CatchingCall
 * @see FailureHandler
 */
internal class SuspendResultCallAdapterFactory(
    private val failureHandler: FailureHandler? = null
) : CallAdapter.Factory() {

    /**
     * Callback interface for reporting failures.
     *
     * Called when any error occurs during network operations:
     * - HTTP errors returned from the server (4xx, 5xx responses)
     * - Network/transport failures (connection timeouts, DNS failures, etc.)
     *
     * Implementations should handle errors appropriately based on the type of
     * [Throwable] received. Common use cases include:
     * - Logging errors for monitoring
     * - Showing error messages to users
     * - Reporting metrics/analytics
     * - Triggering retry logic
     *
     * ### Thread Safety
     * Callbacks are invoked on the network dispatcher thread. Ensure error
     * handling logic is main-safe if needed for UI updates.
     *
     * ### Usage
     * ```kotlin
     * val handler = SuspendResultCallAdapterFactory.FailureHandler { e ->
     *     Log.e("Network", "Request failed: ${e.message}")
     * }
     *
     * val factory = SuspendResultCallAdapterFactory(handler)
     * ```
     *
     * @param throwable The error that occurred:
     *   - [HttpException] for HTTP errors (4xx, 5xx responses)
     *   - Any other [Throwable] for network/transport failures (timeouts,
     *     connection errors, DNS failures, etc.)
     */
    internal fun interface FailureHandler {
        /**
         * Called when a failure occurs during a network request.
         *
         * This method is invoked for both HTTP errors (4xx, 5xx responses)
         * and network/transport failures (timeouts, connection errors, etc.).
         *
         * ### Example
         * ```kotlin
         * factory = SuspendResultCallAdapterFactory(object : FailureHandler {
         *     override fun onFailure(throwable: Throwable) {
         *         when (throwable) {
         *             is HttpException -> handleHttpError(throwable.code())
         *             else -> handleNetworkFailure(throwable)
         *         }
         *     }
         *
         *     private fun handleHttpError(code: Int) {
         *         // Show error dialog, log metrics, etc.
         *     }
         *
         *     private fun handleNetworkFailure(throwable: Throwable) {
         *         // Show "no internet" message, retry logic, etc.
         *     }
         * })
         * ```
         *
         * @param throwable The error that occurred. Do not swallow this parameter
         *   as it may contain critical information needed for error handling,
         *   logging, or analytics.
         */
        fun onFailure(throwable: Throwable)
    }

    /**
     * Caches whether Retrofit has a custom converter for [Result] type.
     *
     * This caching avoids redundant lookups during adapter factory operations.
     * When a custom converter for [Result] exists, all [Call] responses are
     * directly delegated to that converter without wrapping.
     *
     * ### Thread Safety
     * Uses simple caching pattern suitable for single-threaded Retrofit
     * initialization contexts. Not thread-safe for concurrent initialization
     * scenarios.
     *
     * @property hasConverterForResult Cached result indicating whether a
     *   custom converter for [Result] was registered.
     */
    private var hasConverterForResult: Boolean? = null

    /**
     * Checks if Retrofit has a custom converter for [Result] type.
     *
     * Caches the result to avoid redundant lookups. Returns [true] if a converter
     * exists for any `Result<T>` type, which indicates a custom [Result] converter
     * was registered. If no converter exists, returns [false].
     *
     * ### Why this check matters
     * - If a custom [Result] converter is registered, Retrofit should handle
     *   conversion directly without our wrapping logic
     * - This check determines whether to use direct conversion or wrap in
     *   [CatchingCallAdapter]
     *
     * @param retrofit The Retrofit instance to check for custom converters.
     * @param resultType The [Result] type to verify has a converter. This is
     *   typically extracted from a [Call<Result<T>>] return type.
     * @return [true] if a converter for [Result] is found (custom converter
     *   registered), [false] otherwise.
     */
    private fun Retrofit.hasConverterForResultType(resultType: Type): Boolean {
        // If converter exists for any `Result<T>`,
        // user registered custom converter for `Result` type.
        // No need to check again.
        return if (hasConverterForResult == true) true else runCatching {
            nextResponseBodyConverter<Result<*>>(
                null, resultType, arrayOf()
            )
        }.isSuccess.also { hasConverterForResult = it }
    }

    /**
     * Helper [ParameterizedType] representing [Call<T>] where T is extracted from the original
     * return type. This delegates to Retrofit's converter finder while preserving the call type.
     *
     * ### Usage
     * Used when custom [Result] converter is registered to maintain the correct
     * type hierarchy for Retrofit's conversion pipeline.
     *
     * ### Why we need this
     * - Retrofit expects [CallAdapter] implementations to handle the conversion
     * - When a custom converter exists, we need to present the appropriate type
     *   for Retrofit to use
     * - This wrapper preserves the [Call] type while allowing delegation
     *
     * @param dataType The raw type parameter from `Call<Result<dataType>>`, extracted from the
     *   return type for delegation. This is the type T in `Call<Result<T>>`.
     * @property actualTypeArguments Returns an array containing the single type argument
     *   (dataType).
     * @property rawType Returns [Call::class.java] as the raw type.
     * @property ownerType Returns [null] as this class has no owner type.
     */
    private class CallDataType(
        private val dataType: Type
    ) : ParameterizedType {
        override fun getActualTypeArguments(): Array<Type> = arrayOf(dataType)
        override fun getRawType(): Type = Call::class.java
        override fun getOwnerType(): Type? = null
    }

    /**
     * Determines the appropriate adapter type based on whether a custom [Result] converter exists.
     *
     * ### Adaptation Flow
     * 1. Check if return type is [Call<Result<T>>]
     * 2. Extract [Result<T>] and [T] types
     * 3. Check if custom [Result] converter is registered
     * 4. If custom converter exists, delegate directly to it
     * 5. Otherwise, wrap with [CatchingCallAdapter] for our custom handling
     *
     * ### Type Extraction
     * - Returns [null] if returnType is not [Call] or not a [ParameterizedType]
     * - Returns [null] if inner type is not [Result] or not a [ParameterizedType]
     *
     * @param returnType The raw return type from the API method (e.g., [Call<Result<User>>]).
     * @param annotations Annotations from the API method (unused but required by Retrofit).
     * @param retrofit The Retrofit instance for delegate lookups.
     * @return A [CallAdapter] instance, or [null] if the return type is not supported.
     *   Supported return types are `Call<Result<T>>` where T is any type.
     *
     * ### Example
     * ```kotlin
     * // Returns CatchingCallAdapter with appropriate delegate
     * get(Call<Result<User>>:, Array<Annotation>?, Retrofit)
     *
     * // Returns null - not a Call
     * get(Call<String>:, Array<Annotation>?, Retrofit)
     *
     * // Returns null - not Result inside Call
     * get(Call<ResultWrapper<String>>:, Array<Annotation>?, Retrofit)
     * ```
     */
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        // Check if the return type is `Call<Result<T>>`
        if (getRawType(returnType) != Call::class.java) return null
        if (returnType !is ParameterizedType) return null

        // Extract Result<T> from Call<Result<T>>
        val resultType: Type = getParameterUpperBound(0, returnType)
        // Must be Result<something>
        if (getRawType(resultType) != Result::class.java
            || resultType !is ParameterizedType
        ) return null

        // Extract T from Result<T>
        val dataType = getParameterUpperBound(0, resultType)

        val delegateType = if (retrofit.hasConverterForResultType(resultType))
            returnType else CallDataType(dataType)

        val delegate: CallAdapter<*, *> = retrofit
            .nextCallAdapter(this, delegateType, annotations)

        return CatchingCallAdapter(delegate, failureHandler)
    }

    /**
     * Adapts a [Call] to wrap responses in [Result] and delegate conversion.
     *
     * ### Responsibilities
     * - Delegates type conversion to Retrofit's converter
     * - Wraps responses with [Result] wrapper
     * - Invokes [FailureHandler] on errors (via nested [CatchingCall])
     *
     * ### Type Parameters
     * - [A]: Unused type parameter (required by CallAdapter interface)
     * - [Call<Result<*>]>: Adapted return type
     *
     * @property delegate The underlying [CallAdapter] for type conversion. When
     *   a custom [Result] converter is registered, delegates directly to it.
     *   Otherwise, delegates to a [CallDataType] wrapper.
     * @property failureHandler The optional failure handler passed to the factory.
     *   Passed through to nested [CatchingCall] instances.
     *
     * @see CatchingCall
     */
    private class CatchingCallAdapter(
        private val delegate: CallAdapter<*, *>,
        private val failureHandler: FailureHandler?
    ) : CallAdapter<Any, Call<Result<*>>> {
        override fun responseType(): Type = delegate.responseType()
        override fun adapt(call: Call<Any>): Call<Result<*>> = CatchingCall(call, failureHandler)
    }

    /**
     * Retrofit-wrapped [Call] that intercepts responses and wraps them in [Result].
     *
     * ### Response Handling
     * - **Successful response** ([Response.isSuccessful] == true):
     *   - Extracts body from [Response.body()]
     *   - Wraps in [Result.success(body)]
     *   - Returns via [Response.success(Result.success(body))]
     *
     * - **HTTP error** ([Response.isSuccessful] == false):
     *   - Creates [HttpException] from [Response]
     *   - Invokes [FailureHandler.onFailure()](-FailureHandler.html#onFailure(throwable)) if registered
     *   - Wraps in [Result.failure(httpException)]
     *   - Returns via [Response.success(Result.failure(httpException))]
     *
     * - **Network failure** ([onFailure()](-Callback.html#onFailure(Call,Throwable)) called):
     *   - Invokes [FailureHandler.onFailure()](-FailureHandler.html#onFailure(throwable)) if registered
     *   - Wraps [Throwable] in [Result.failure(throwable)]
     *   - Returns via [Response.success(Result.failure(throwable))]
     *
     * ### Important Notes
     * - Always returns [Response.success()](-Response.html#success(T)) even for errors
     * - This is required by Retrofit's callback contract
     - The actual result (success/failure) is in [Response.body()](-Response.html#body())
     *
     * ### Threading
     * - [enqueue](-Call.html#enqueue(Callback)) callbacks execute on network dispatcher thread
     * - [onFailure](-FailureHandler.html#onFailure(throwable)) is invoked synchronously on same thread
     *
     * @property delegate The underlying [Call] being wrapped.
     * @property failureHandler The failure handler for reporting errors.
     *
     * ### Example
     * ```kotlin
     * val call = retrofit.create(Api::class.java).getUser("123")
     *
     * // Successful response
     * call.enqueue(object : Callback<Result<User>> {
     *     override fun onResponse(call: Call<Result<User>>, response: Response<Result<User>>) {
     *         if (response.isSuccessful) {
     *             val user = response.body()?.getOrNull()
     *             // Handle success
     *         }
     *     }
     *
     *     override fun onFailure(call: Call<Result<User>>, t: Throwable) {
     *         // Network error
     *     }
     * })
     * ```
     */
    private class CatchingCall(
        private val delegate: Call<Any>,
        private val failureHandler: FailureHandler?
    ) : Call<Result<*>> {

        /**
         * Enqueues the call with a callback that intercepts responses.
         *
         * ### Implementation Details
         * - Creates a [Callback<Any>](-)Callback.html#onResponse(Call,Response) that handles
         *   the response from the delegate
         * - [Response.isSuccessful](-Response.html#isSuccessful) determines success/failure
         * - [Response.body()](-Response.html#body()) contains the wrapped result
         *
         * @param callback The [Callback<Result<*>>](--Callback-result-) to invoke with results.
         */
        override fun enqueue(callback: Callback<Result<*>>) = delegate.enqueue(object : Callback<Any> {
            /**
             * Called when the request completes successfully or with an HTTP error.
             *
             * ### Behavior
             * - If [response](--Response-) is successful:
             *   - Extract body from [Response.body()](--Response-body-)
             *   - Wrap in [Result.success(body)](--Result-success-)
             *   - Invoke callback's [onResponse()](--Callback-onResponse-) with wrapped result
             *
             * - If [response](--Response-) is not successful:
             *   - Create [HttpException](--HttpException-) from [Response](--Response-)
             *   - Invoke [failureHandler.onFailure()](--FailureHandler-onFailure-) if registered
             *   - Wrap in [Result.failure(httpException)](--Result-failure-)
             *   - Invoke callback's [onResponse()](--Callback-onResponse-) with wrapped result
             *
             * ### Thread Safety
             * This method executes on the network dispatcher thread. Ensure callback
             * logic is appropriately handled (e.g., post to main thread for UI updates).
             *
             * @param call The [Call](--Call-) that completed.
             * @param response The [Response](--Response-) object containing result or error.
             */
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    callback.onResponse(this@CatchingCall, Response.success(Result.success(body)))
                } else {
                    val throwable = HttpException(response)
                    failureHandler?.onFailure(throwable)
                    callback.onResponse(
                        this@CatchingCall,
                        Response.success(Result.failure<Any>(throwable))
                    )
                }
            }

            /**
             * Called when a network-level failure occurs (not an HTTP error).
             *
             * ### When is this called
             * - Connection timeouts
             * - DNS failures
             * - Socket exceptions
             * - TLS handshake failures
             * - Any other transport layer errors
             *
             * ### Behavior
             * - Invokes [failureHandler.onFailure()](--FailureHandler-onFailure-) if registered
             * - Wraps the [Throwable](--Throwable-) in [Result.failure(throwable)](--Result-failure-)
             * - Invokes callback's [onResponse()](--Callback-onResponse-) with wrapped result
             * - Note: Uses [onResponse](--Callback-onResponse-) instead of [onFailure](--Callback-onFailure-)
             *   to maintain consistent callback signature
             *
             * ### Thread Safety
             * This method executes on the network dispatcher thread. Ensure callback
             * logic is appropriately handled (e.g., post to main thread for UI updates).
             *
             * @param call The [Call](--Call-) that failed.
             * @param t The [Throwable](--Throwable-) representing the network failure.
             */
            override fun onFailure(call: Call<Any>, t: Throwable) {
                failureHandler?.onFailure(t)
                callback.onResponse(
                    this@CatchingCall,
                    Response.success(Result.failure<Any>(t))
                )
            }
        })

        override fun clone(): Call<Result<*>> = CatchingCall(delegate, failureHandler)
        override fun execute(): Response<Result<*>> =
            throw UnsupportedOperationException("Suspend function should not be blocking.")
        override fun isExecuted(): Boolean = delegate.isExecuted
        override fun cancel(): Unit = delegate.cancel()
        override fun isCanceled(): Boolean = delegate.isCanceled
        override fun request(): Request = delegate.request()
        override fun timeout(): Timeout = delegate.timeout()
    }
}
