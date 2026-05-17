package com.mvi.core.navigation

abstract class FeatureRoute<T : Any> {
    abstract val route: T
    open val navOptions: FeatureNavOptions? = null
}
