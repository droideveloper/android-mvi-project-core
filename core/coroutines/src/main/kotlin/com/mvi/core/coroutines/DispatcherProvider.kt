package com.mvi.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {

    val ui: CoroutineDispatcher

    val io: CoroutineDispatcher

    val computation: CoroutineDispatcher
}
