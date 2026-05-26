package com.mvi.core.navigation

/**
 * Abstract base class for feature route definitions.
 *
 * A [FeatureRoute] represents a navigation destination in the app's navigation graph.
 * It provides a type-safe way to define routes that can be navigated to, popped from,
 * and restored in the navigation stack.
 *
 * @param T The type parameter for the route identifier (typically a String or enum)
 */
abstract class FeatureRoute<T : Any> {
    /**
     * The route identifier for this feature.
     *
     * This is typically a String or enum that uniquely identifies this route
     * in the navigation graph. Used for navigation commands like navigate to,
     * pop to, or pop up to this route.
     */
    abstract val route: T

    /**
     * Optional navigation options for this route.
     *
     * This provides route-specific configuration for navigation behavior such as
     * whether to save state, pop up to this route, or treat it as inclusive.
     * If null, default navigation options will be used.
     */
    open val navOptions: FeatureNavOptions? = null
}
