package com.mvi.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Default implementation of [DispatcherProvider] using Android's built-in dispatchers.
 *
 * Provides access to standard coroutine dispatchers:
 * - [ui]: [Dispatchers.Main] for UI operations
 * - [io]: [Dispatchers.IO] for I/O operations
 * - [computation]: [Dispatchers.Default] for CPU-bound tasks
 */
class DispatcherProviderImpl @Inject constructor() : DispatcherProvider {

    override val ui: CoroutineDispatcher
        get() = Dispatchers.Main

    override val io: CoroutineDispatcher
        get() = Dispatchers.IO

    override val computation: CoroutineDispatcher
        get() = Dispatchers.Default
}
