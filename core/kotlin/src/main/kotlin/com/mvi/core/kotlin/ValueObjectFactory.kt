package com.mvi.core.kotlin

abstract class ValueObjectFactory<in P, out V, out T: IllegalArgumentException> {
    protected abstract val initializer: (P) -> V
    open fun getThrowable(input: P): T? = null
    abstract fun isValid(input: P): Boolean

    public fun get(input: P): Result<V> = when {
        isValid(input) -> Result.success(initializer(input))
        else -> Result.failure(getThrowable(input) ?: IllegalArgumentException())
    }

    public fun getOrNull(input: P): V? = get(input).getOrNull()
    public fun getOrThrow(input: P): V = get(input).getOrThrow()
}
