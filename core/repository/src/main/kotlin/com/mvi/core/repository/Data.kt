package com.mvi.core.repository

/**
 * Functional interface for data sources that perform async operations.
 *
 * Encapsulates the logic to fetch data from external sources (network, database, etc.).
 * Each execution is independent and produces a [Result] that may succeed or fail.
 *
 * @param Input The type of input required to execute the source operation.
 * @param Output The type of data returned on successful execution.
 */
interface DataSource<in Input, out Output> {
    /**
     * Invokes the data source operation.
     *
     * Executes the async data fetching logic and returns the result.
     * If the operation fails (network error, timeout, etc.), returns a [Result.Failure].
     *
     * @param input The input parameters required to execute the operation.
     * @return A [Result] containing either the [Output] data or an exception.
     */
    suspend operator fun invoke(input: Input): Result<Output>
}

/**
 * Functional interface for synchronous data mapping operations.
 *
 * Encapsulates the logic to transform raw input data into a structured output.
 * Used for converting, validating, or normalizing data within the repository layer.
 *
 * @param Input The type of input data to be transformed.
 * @param Output The type of transformed output data.
 */
interface DataMapper<in Input, out Output> {
    /**
     * Maps input data to the expected output structure.
     *
     * This operation should handle internal mapping logic including validation
     * or normalization. If invalid input is provided, this may throw or return
     * a failure wrapped appropriately (depending on implementation).
     *
     * @param input The input data to transform.
     * @return The transformed output data.
     */
    fun map(input: Input): Output
}

/**
 * Core repository interface defining the contract for data persistence and retrieval.
 *
 * Combines data source operations with data mapping to provide a unified query API.
 * The repository accepts input parameters, applies criteria-based filtering,
 * and returns a [Result] containing the transformed output or an exception.
 *
 * The `clear` method provides a way to invalidate cached or cleared data for the given input.
 *
 * @param Input The type of input used to identify and query specific data entities.
 * @param Output The type of data returned after mapping from the source.
 */
interface Repository<in Input, out Output> {
    /**
     * Queries and retrieves data based on the provided input and criteria.
     *
     * Execution Flow:
     * 1. Invoke the underlying [DataSource] with the given [input]
     * 2. Evaluate [criteria] to determine freshness and validity
     * 3. Apply [DataMapper] if available to transform raw data
     * 4. Return [Result.Success(Output)] or [Result.Failure]
     *
     * @param input The input parameters identifying the data to query.
     * @param criteria The freshness criteria to evaluate cached or live data.
     *                 Determines whether to use cached or fetch fresh data.
     * @return A [Result] containing the transformed output or an exception.
     */
    suspend fun query(input: Input, criteria: Criteria): Result<Output>

    /**
     * Clears the cached or persisted data for the given input.
     *
     * Use this method to invalidate existing cache entries, force fresh
     * retrievals, or reset state for the next query operation.
     *
     * @param input The input parameters identifying which data to clear.
     */
    suspend fun clear(input: Input)
}

/**
 * Extension function for [Repository] where input is [Unit].
 *
 * Provides a simplified query interface when no input parameters are needed.
 * The input is fixed as [Unit] and omitted from the method signature.
 *
 * @param criteria The freshness criteria to evaluate data validity.
 * @return A [Result] containing the output data or an exception.
 */
suspend fun <Output> Repository<Unit, Output>.query(criteria: Criteria) = query(input = Unit, criteria)

/**
 * Extension function for [Repository] where input is [Unit].
 *
 * Provides a simplified clear interface when no input parameters are needed.
 * The input is fixed as [Unit] and omitted from the method signature.
 */
suspend fun <Output> Repository<Unit, Output>.clear() = clear(input = Unit)
