package com.mvi.core.app

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
interface ContextModule {

    @Binds
    fun bindContext(application: Application): Context

    companion object {

        @Reusable
        @Provides
        fun bindOrientationProvider(impl: OrientationProviderImpl): OrientationProvider = impl
    }
}
