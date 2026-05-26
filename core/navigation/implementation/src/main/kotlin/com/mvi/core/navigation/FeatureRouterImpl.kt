package com.mvi.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import javax.inject.Inject

/**
 * Default implementation of [FeatureRouter] for Android [NavController].
 *
 * Delegates navigation requests to the provided [NavController], converting
 * [FeatureRoute] and [FeatureNavOptions] into standard NavOptions.
 *
 * ## Usage
 * ```kotlin
 * // Inject into ViewModel or use case
 * @Inject
 * lateinit var router: FeatureRouter
 *
 * // Navigate to a route
 * router.navigate(homeRoute)
 *
 * // Navigate with options
 * router.navigate(
 *     settingsRoute,
 *     FeatureNavOptions(singleTop = true)
 * )
 *
 * // Restart a route (equivalent to navigate with popUpTo)
 * router.restart(settingsRoute)
 *
 * // Go back
 * router.back()
 * ```
 *
 * ## Implementation Details
 * - Converts [FeatureNavOptions] to [NavOptions] via [toOptions]
 * - Handles [popUpTo](FeatureNavOptions.popUpTo) navigation with inclusive/saveState
 * - Supports singleTop destinations when requested
 *
 * @param navController The NavController to delegate navigation to
 */
internal class FeatureRouterImpl @Inject constructor(
    private val navController: NavController,
) : FeatureRouter {

    /**
     * Navigate to a route, optionally using navOptions defined on the route.
     *
     * If [FeatureRoute.navOptions] is set, those options take precedence.
     * Otherwise, navigates with default NavOptions.
     *
     * @param route The feature route to navigate to
     */
    override fun <T : Any> navigate(route: FeatureRoute<T>) = when {
        route.navOptions != null -> {
            val navOptions = requireNotNull(route.navOptions).toOptions()
            navController.navigate(route.route, navOptions)
        }
        else -> navController.navigate(route.route)
    }

    /**
     * Navigate to a route with the provided navigation options.
     *
     * @param route The feature route to navigate to
     * @param options The navigation options to use
     */
    override fun <T : Any> navigate(route: FeatureRoute<T>, options: FeatureNavOptions) {
        navController.navigate(route.route, options.toOptions())
    }

    /**
     * Restart a route by navigating to it with options to pop up to the parent.
     *
     * Equivalent to [navigate](FeatureRouter.navigate) followed by popping back
     * to the parent. Useful for resetting a destination's state without losing
     * the back stack to navigate there.
     *
     * @param route The feature route to restart
     */
    override fun <T : Any> restart(route: FeatureRoute<T>) =
        navigate(route)

    /**
     * Navigate back to the previous destination in the back stack.
     *
     * Equivalent to calling [NavController.popBackStack] without arguments.
     */
    override fun back() {
        navController.popBackStack()
    }

    /**
     * Convert [FeatureNavOptions] to a standard [NavOptions] instance.
     *
     * @return NavOptions built from the feature options
     */
    private fun FeatureNavOptions.toOptions(): NavOptions = navOptions {
        restoreState = this@toOptions.restoreState
        launchSingleTop = this@toOptions.singleTop
        this@toOptions.popUpTo?.let { route ->
            popUpTo(route = route) {
                inclusive = this@toOptions.inclusive
                saveState = this@toOptions.saveState
            }
        }
    }
}
