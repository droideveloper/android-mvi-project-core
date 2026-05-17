package com.mvi.core.coroutines

import dagger.Binds
import dagger.Module

@Module
interface CoroutinesModule {

    @Binds
    fun bindDispatcherProvider(impl: DispatcherProviderImpl): DispatcherProvider
}
