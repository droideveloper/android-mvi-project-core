package com.mvi.core.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Core repository implementation that transforms data from a source through caching.
 *
 * This repository follows a cache-then-source pattern, where data is retrieved from
 * a local cache first (if valid), and only falls back to the source when:
 * - The cache is empty, or
 * - The cached data has exceeded its configured age (based on [Criteria.Timed])
 *
 * Thread-safe: Uses a [Mutex] to ensure concurrent cache operations are atomic.
 *
 * @param factory A function to create a [Cache] for a given input.
 * @param source The data source that provides raw model data.
 * @param mapper A function to transform raw model data into the final output.
 * @property caches A map storing [Cache] instances per input key.
 * @property mutex A [Mutex] used for thread-safe cache operations.
 */
internal class TransformingRepository<in Input, in Model, out Output>(
    /**
     * Factory function to create a cache instance for a given input.
     * Used to lazily initialize per-input caches.
     */
    private val factory: (Input) -> Cache<Model>,
    /**
     * The asynchronous data source for fetching raw model data.
     */
    private val source: DataSource<Input, Model>,
    /**
     * The mapper used to transform raw model data into the final output.
     * Must handle any transformation logic including validation or normalization.
     */
    private val mapper: DataMapper<Model, Output>,
) : Repository<Input, Output> {

    /**
     * Ensures thread-safe cache creation and retrieval per input.
     * Uses a [Mutex] to prevent concurrent modification of the same cache.
     */
    private val mutex = Mutex()
    /**
     * Stores [Cache] instances keyed by input.
     * Each cache is created once per unique input value.
     */
    private val caches = mutableMapOf<Input, Cache<Model>>()

    /**
     * Queries the repository with the given input and criteria.
     *
     * Flow:
     * 1. Obtain or create cache for the input (thread-safe)
     * 2. Check cache against [Criteria]:
     *    - If criteria is not [Timed], skip age check
     * 3. If cache has valid data: map and return
     * 4. Otherwise: fetch from source, cache, map, and return
     *
     * @param input The input identifying the data to query.
     * @param criteria The freshness criteria determining cache validity.
     * @return A [Result] containing the transformed output or failure.
     */
    override suspend fun query(input: Input, criteria: Criteria): Result<Output> =
        query(cache = getOrCreateCache(input), input, criteria)

    /**
     * Clears the cached data for the specified input.
     *
     * Executes within a [Mutex] lock to prevent concurrent modification.
     * After clearing, subsequent queries will trigger a fresh fetch from source.
     *
     * @param input The input identifying which cache to clear.
     */
    override suspend fun clear(input: Input) {
        mutex.withLock {
            caches[input]?.clear()
        }
    }

    /**
     * Checks cache against criteria and retrieves or refreshes data.
     *
     * - [Criteria.Timed]: Checks cache age and uses source if expired
     * - Other criteria: Uses cached value if present
     *
     * @param cache The cache to check against.
     * @param input The input identifying the cache entry.
     * @param criteria The freshness criteria to evaluate.
     * @return A [Result] with the transformed output.
     */
    private suspend fun query(cache: Cache<Model>, input: Input, criteria: Criteria): Result<Output> =
        when (criteria) {
            is Criteria.Timed ->
                cache
                    .get(maxAge = criteria.maxAge)
                    .fold(
                        onSuccess = { json ->
                            val data = mapper.map(json)
                            Result.success(data)
                        },
                        onFailure = {
                            source(input)
                                .onSuccess { cache.put(it) }
                                .mapCatching(mapper::map)
                        }
                    )
        }

    /**
     * Creates or returns the existing cache for a given input.
     *
     * Uses [Mutex] to ensure atomicity during cache creation.
     * If a cache doesn't exist, creates one using the [factory].
     *
     * @param input The input key for cache retrieval.
     * @return The existing or newly created [Cache] instance.
     */
    private suspend fun getOrCreateCache(input: Input): Cache<Model> =
        mutex.withLock {
            caches[input] ?: factory(input).also {
                caches[input] = it
            }
        }
}

/**
 * Creates a passthrough mapper that returns input as output.
 *
 * Used for repositories where no transformation is needed.
 * Maps output to the same type (identity transformation).
 *
 * @return A [DataMapper] that performs identity mapping.
 */
internal fun <Output> passthroughMapper() = object : DataMapper<Output, Output> {
    override fun map(input: Output): Output = input
}

/**
 * Factory function to create a repository with passthrough mapping.
 *
 * Used when no data transformation is needed (output equals model).
 *
 * @param factory A function to create a cache for a given input.
 * @param source The data source providing raw data.
 * @return A [Repository] with passthrough mapping configured.
 */
fun <Input, Output> createRepository(
    factory: (Input) -> Cache<Output>,
    source: DataSource<Input, Output>,
): Repository<Input, Output> = TransformingRepository(
    factory = factory,
    source = source,
    mapper = passthroughMapper(),
)

/**
 * Factory function to create a repository with custom mapping.
 *
 * Used when data transformation is required from Model to Output.
 *
 * @param factory A function to create a cache for a given input.
 * @param source The data source providing raw model data.
 * @param mapper A mapper to transform Model to Output.
 * @return A [Repository] with custom mapping configured.
 */
fun <Input, Model, Output> createRepository(
    factory: (Input) -> Cache<Model>,
    source: DataSource<Input, Model>,
    mapper: DataMapper<Model, Output>,
): Repository<Input, Output> = TransformingRepository(
    factory = factory,
    source = source,
    mapper = mapper,
)
