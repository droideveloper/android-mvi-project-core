package com.mvi.core.app

import android.app.Application
import android.content.Context
import com.mvi.core.coroutines.CoroutinesModule
import com.mvi.core.coroutines.DispatcherProvider
import com.mvi.core.environment.Environment
import com.mvi.core.environment.EnvironmentModule
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        ContextModule::class,
        CoroutinesModule::class,
        EnvironmentModule::class,
    ]
)
interface AppComponent {

    val context: Context

    val dispatcherProvider: DispatcherProvider

    val orientationProvider: OrientationProvider

    val environment: Environment

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application,
        ): AppComponent
    }

    companion object {
        fun create(
            application: Application,
        ) = DaggerAppComponent
                .factory()
                .create(application)
    }
}
