package com.mvi.core.location

import android.content.Context
import com.google.android.gms.location.LocationRequest
import com.mvi.core.environment.Environment
import dagger.Module
import dagger.Provides

@Module
interface LocationModule {

    val locationProvider: LocationProvider

    companion object {

        @Provides
        fun provideLocationRequest(): LocationRequest = LocationRequest.Builder(
            60 * 60 * 1000
        ).build()

        @Provides
        fun provideContextProvider(context: Context): () -> Context = { context }

        @Provides
        fun bindLocationProvider(impl: LocationProviderImpl, env: Environment): LocationProvider =
            when {
                env.isMock -> MockLocationProvider
                else -> impl
            }
    }
}
