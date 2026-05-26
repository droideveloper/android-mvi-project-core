package com.mvi.core.navigation

/**
 * Interface for feature navigation routing.
 *
 * Methods to navigate to routes, restart navigation, and go back.
 */
interface FeatureRouter {

    /**
     * Navigate to a feature route.
     *
     * @param route The route to navigate to
     */
    fun <T : Any> navigate(route: FeatureRoute<T>)

    /**
     * Navigate to a feature route with navigation options.
     *
     * @param route The route to navigate to
     * @param options Navigation options for this route
     */
    fun <T : Any> navigate(route: FeatureRoute<T>, options: FeatureNavOptions)

    /**
     * Restart navigation from a specific route.
     *
     * @param route The route to restart from
     */
    fun <T : Any> restart(route: FeatureRoute<T>)

    /**
     * Go back to the previous route in the navigation stack.
     */
    fun back()
}
