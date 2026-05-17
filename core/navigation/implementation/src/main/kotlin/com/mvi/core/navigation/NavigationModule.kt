package com.mvi.core.navigation

import dagger.Module
import dagger.Provides

@Module
interface NavigationModule {

    val featureRouter: FeatureRouter

    companion object {

        @Provides
        internal fun bindFeatureRouter(impl: FeatureRouterImpl): FeatureRouter = impl
    }
}
