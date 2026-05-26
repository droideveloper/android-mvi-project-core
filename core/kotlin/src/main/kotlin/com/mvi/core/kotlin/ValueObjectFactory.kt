package com.mvi.core.kotlin

/**
 * Abstract factory class for creating validated value objects with support for custom error types.
 *
 * Provides a common interface for building value objects that validate input data and handle
 * validation failures through result types or optional throwable.
 *
 * @param P The input type for validation
 * @param V The validated value type returned on success
 * @param T The custom throwable type for validation errors (defaults to [IllegalArgumentException])
 *
 * @property initializer A lambda that transforms valid input into the resulting value
 */
abstract class ValueObjectFactory<in P, out V, out T : IllegalArgumentException> {

    /**
     * The initializer lambda that converts valid input into the resulting value.
     *
     * @param input The validated input to transform into a value object
     */
    protected abstract val initializer: (P) -> V

    /**
     * Retrieves a custom throwable for the given invalid input.
     *
     * This method allows customization of error types. If [getThrowable] returns null,
     * the factory will default to [IllegalArgumentException].
     *
     * @param input The invalid input for which to provide a custom error
     * @return A custom throwable instance or null to use the default exception
     */
    open fun getThrowable(input: P): T? = null

    /**
     * Validates whether the given input passes the value object's validation rules.
     *
     * @param input The input to validate
     * @return true if the input is valid, false otherwise
     */
    abstract fun isValid(input: P): Boolean

    /**
     * Creates a result containing either the validated value or a failure.
     *
     * @param input The input to validate and transform
     * @return A [Result] containing either [Result.success] with the validated value
     * or [Result.failure] with an appropriate error
     */
    public fun get(input: P): Result<V> = when {
        isValid(input) -> Result.success(initializer(input))
        else -> Result.failure(getThrowable(input) ?: IllegalArgumentException())
    }

    /**
     * Retrieves the validated value if validation passes, or null on failure.
     *
     * @param input The input to validate and transform
     * @return The validated value if valid, null otherwise
     */
    public fun getOrNull(input: P): V? = get(input).getOrNull()

    /**
     * Retrieves the validated value if validation passes, or throws an exception on failure.
     *
     * @param input The input to validate and transform
     * @return The validated value if valid
     * @throws T The appropriate error if validation fails, or [IllegalArgumentException]
     */
    public fun getOrThrow(input: P): V = get(input).getOrThrow()
}
