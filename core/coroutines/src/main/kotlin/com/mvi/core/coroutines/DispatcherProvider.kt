package com.mvi.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Interface for providing coroutine dispatchers used throughout the application.
 *
 * This interface defines the contract for accessing different dispatchers optimized
 * for various types of coroutines:
 *
 * @property ui The dispatcher for UI-related coroutines. Typically the Main dispatcher
 *              on Android for executing UI updates.
 * @property io The dispatcher for I/O-bound coroutines. Uses Dispatchers.IO which is
 *              optimized for disk and network operations.
 * @property computation The dispatcher for CPU-bound coroutines. Uses Dispatchers.Default
 *              which is optimized for parallel CPU computations.
 */
interface DispatcherProvider {

    /**
     * The dispatcher for UI-related coroutines.
     *
     * Typically returns [Dispatchers.Main] on Android, which should be used
     * for all UI updates and user interactions.
     */
    val ui: CoroutineDispatcher

    /**
     * The dispatcher for I/O-bound coroutines.
     *
     * Uses [Dispatchers.IO] which is configured with an unbounded off-cpu pool
     * optimized for disk and network operations.
     */
    val io: CoroutineDispatcher

    /**
     * The dispatcher for CPU-bound coroutines.
     *
     * Uses [Dispatchers.Default] which is configured with an unbounded cpu pool
     * optimized for parallel CPU computations.
     */
    val computation: CoroutineDispatcher
}
