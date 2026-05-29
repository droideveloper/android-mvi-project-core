package com.mvi.core.repository

import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

/**
 * Core caching abstraction interface.
 *
 * Defines the contract for any cache implementation within the repository layer.
 * This interface abstracts the details of caching, allowing the consumer to request
 * data with specific freshness constraints without knowing the underlying storage
 * mechanism (e.g., memory, disk, or remote storage).
 *
 * @property T The type of the data stored in the cache.
 */
interface Cache<T> {

    /**
     * Attempts to retrieve a value from the cache.
     *
     * The retrieval is conditional on the age of the cached data relative to
     * the current time. If the cached value is found and is younger than
     * [maxAge], it is returned. Otherwise, a [Result.Failure] is produced.
     *
     * @param maxAge The maximum duration the cached value is allowed to exist
     *               before being considered expired.
     * @return A [Result] containing the cached value if valid.
     *         Returns [Result.Failure] if:
     *         - No value exists (Empty).
     *         - The value exists but exceeds [maxAge] (Expired).
     *         - An internal error occurred.
     */
    suspend fun get(maxAge: Duration): Result<T>

    /**
     * Stores a new value into the cache.
     *
     * Upon storage, the cache is responsible for associating the current time
     * with the provided value, establishing the age baseline for future
     * [get] operations.
     *
     * @param value The data to be cached.
     */
    suspend fun put(value: T)

    /**
     * Invalidates the entire cache.
     *
     * Clears all stored values and resets the internal state.
     * Subsequent calls to [put] will begin the validity period anew.
     */
    suspend fun clear()
}

/**
 * Exception hierarchy for reporting cache operation failures.
 * Inherits from [IllegalStateException] as these exceptions represent
 * a broken cache state.
 */
sealed class CacheException : IllegalStateException() {

    /**
     * Indicates the cache is empty and no value is available.
     *
     * @property message The optional exception message.
     */
    data class Empty(override val message: String?) : CacheException()

    /**
     * Indicates the cached value exists but has exceeded its validity age.
     *
     * @property message The optional exception message.
     */
    data class Expired(override val message: String?) : CacheException()
}

/**
 * Single-value in-memory cache implementation.
 *
 * Provides a simple cache mechanism that stores one value at a time with
 * time-based expiration. This is not a multi-value cache (e.g., LRU);
 * each new [put] call overwrites the previous value.
 *
 * Uses [TimeSource] to generate a timestamp for each cached value,
 * enabling accurate age calculations for [get] validation.
 *
 * @param timeSource The [TimeSource] instance used to track time for expiration checks.
 * @property cache The internal storage holding the cached value and its timestamp.
 *                 Null indicates the cache is empty.
 */
internal class InMemoryCache<T>(
    /**
     * Time source used to calculate age of cached values.
     * Provides accurate timestamp information for validity checks.
     */
    private val timeSource: TimeSource,
) : Cache<T> {

    /**
     * The internal storage pair holding the cached value and its creation timestamp.
     *
     * - Null: Cache is empty
     * - Valid value: Contains the cached data and [TimeMark]
     *
     * This property is mutable and replaced on every [put] call.
     */
    private var cache: Pair<T, TimeMark>? = null

    /**
     * Retrieves a cached value if it exists and is not expired.
     *
     * Validation Logic:
     * 1. If [cache] is null → Return [CacheException.Empty]
     * 2. If [cache] exists but [mark.elapsedNow() > maxAge] → Return [CacheException.Expired]
     * 3. Otherwise → Return the cached value wrapped in [Result.Success]
     *
     * @param maxAge The maximum acceptable age for the cached value.
     * @return A [Result] containing either the cached value or a failure indicating:
     *         - Empty cache (no value stored)
     *         - Expired cache (value exceeds maxAge)
     */
    override suspend fun get(maxAge: Duration): Result<T> =
        cache
            ?.let { (value, mark) ->
                value.takeIf { mark.elapsedNow() <= maxAge }
            }
            ?.let { Result.success(it) }
            ?: when {
                cache == null -> Result.failure(CacheException.Empty("No cache is found."))
                else -> Result.failure(CacheException.Expired("Cache is expired."))
            }

    /**
     * Stores a value in the cache, replacing any existing value.
     *
     * Each call to [put] creates a new entry with the current timestamp from
     * [TimeSource.markNow()]. The previous value is lost and cannot be retrieved.
     *
     * @param value The value to store in the cache.
     */
    override suspend fun put(value: T) {
        // Overwrite previous value with new value and timestamp
        cache = Pair(value, timeSource.markNow())
    }

    /**
     * Clears the cache by resetting the internal state.
     *
     * After this call, [cache] will be null and subsequent [put] calls
     * will begin with an empty cache state.
     */
    override suspend fun clear() {
        cache = null
    }
}
