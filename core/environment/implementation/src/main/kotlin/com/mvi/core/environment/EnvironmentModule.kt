package com.mvi.core.environment

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface EnvironmentModule {

    @Binds
    fun bindEnvironment(impl: EnvironmentImpl): Environment

    companion object {

        @Provides
        fun provideDevice(context: Context): Device = when {
            context.resources.getBoolean(R.bool.isTablet) -> Device.Tablet
            else -> Device.Phone
        }
    }
}
