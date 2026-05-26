package com.mvi.core.navigation

/**
 * Container for navigation options that control the behavior of feature routes.
 *
 * This class holds configuration for how a route should be navigated to, including whether
 * it should be treated as a single top instance, whether to save/restore state, and whether
 * it should be inclusive in navigation stack operations.
 */
class FeatureNavOptions internal constructor(
    /**
     * Whether this route should be the single top instance in the back stack.
     *
     * When true, any other back stack entries are popped when navigating to this route,
     * and any subsequent navigation to this route pops the previous instance.
     */
    val singleTop: Boolean,

    /**
     * Whether to save and restore state (ViewModels) when navigating away from and back to this route.
     *
     * When true, the destination's state is saved when navigating to a different destination
     * and restored when returning. When false, destinations are recreated from scratch.
     */
    val restoreState: Boolean,

    /**
     * Whether this destination should be inclusive when using inclusive navigation.
     *
     * When true, navigation methods like [navigateUp] will stop at this destination
     * when navigating from below it. Default is false.
     */
    val inclusive: Boolean,

    /**
     * Whether to save state when navigating away from this destination.
     *
     * When true, the destination's state is saved so it can be restored later.
     * This is typically used in conjunction with [restoreState].
     */
    val saveState: Boolean,

    /**
     * The route to pop up to when using [popUpTo] navigation.
     *
     * This is used to define a "pop up to" destination, which causes all routes
     * above this one to be popped when navigating to a new destination.
     * Can be null for default behavior.
     */
    val popUpTo: Any?,
) {

    /**
     * Builder for creating [FeatureNavOptions] instances.
     *
     * All parameters are optional and default to false/null, which means standard
     * navigation behavior will be used.
     */
    data class Builder(
        internal var singleTop: Boolean = false,
        internal var restoreState: Boolean = false,
        internal var inclusive: Boolean = false,
        internal var saveState: Boolean = false,
        internal var popUpTo: Any? = null,
    ) {

        /**
         * Sets whether this route should be the single top instance.
         *
         * @param singleTop True if this route should be treated as single top
         * @return This builder for method chaining
         */
        fun singleTop(singleTop: Boolean): Builder = apply { this.singleTop = singleTop }

        /**
         * Sets whether state should be restored when returning to this route.
         *
         * @param restoreState True if state should be restored
         * @return This builder for method chaining
         */
        fun restoreState(restoreState: Boolean): Builder = apply { this.restoreState = restoreState }

        /**
         * Sets whether this destination should be inclusive in navigation.
         *
         * @param inclusive True if this destination should be inclusive
         * @return This builder for method chaining
         */
        fun inclusive(inclusive: Boolean): Builder = apply { this.inclusive = inclusive }

        /**
         * Sets whether state should be saved when navigating away from this route.
         *
         * @param saveState True if state should be saved
         * @return This builder for method chaining
         */
        fun saveState(saveState: Boolean): Builder = apply { this.saveState = saveState }

        /**
         * Sets the route to pop up to when navigating.
         *
         * @param popUpTo The route to pop up to, or null for default behavior
         * @return This builder for method chaining
         */
        fun popUpTo(popUpTo: Any?): Builder = apply { this.popUpTo = popUpTo }

        /**
         * Builds the [FeatureNavOptions] instance with the current settings.
         *
         * @return A new [FeatureNavOptions] instance
         */
        fun build() = FeatureNavOptions(
            singleTop = singleTop,
            restoreState = restoreState,
            inclusive = inclusive,
            saveState = saveState,
            popUpTo = popUpTo,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other === this) return true
        if (other is FeatureNavOptions) {
            return other.singleTop == singleTop
                && other.restoreState == restoreState
                && other.inclusive == inclusive
                && other.saveState == saveState
                && other.popUpTo == popUpTo
        }
        return false
    }

    override fun hashCode(): Int {
        var result = singleTop.hashCode()
        result = 31 * result + restoreState.hashCode()
        result = 31 * result + inclusive.hashCode()
        result = 31 * result + saveState.hashCode()
        result = 31 * result + (popUpTo?.hashCode() ?: 0)
        return result
    }
}
