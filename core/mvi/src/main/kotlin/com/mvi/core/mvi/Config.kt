package com.mvi.core.mvi

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Configuration settings for the MVI module.
 *
 * This object provides global configuration values that can be used
 * throughout the MVI implementation, such as default timeouts and delays.
 */
object Config {
    /**
     * The default delay duration for throttled events.
     *
     * Used by [FlowMviViewModel.onClick] and [MviViewModel.onClick] to
     * prevent rapid-fire event handling and ensure user actions are processed
     * with appropriate debouncing.
     *
     * @see Config.defaultDelay
     */
    val defaultDelay: Duration = 300.milliseconds
}
