package com.mvi.core.environment
/**
 * Represents the detected device type for feature flagging and UI adaptations.
 * Detection is based on [android.R.bool.isTablet] resource value.
 */
sealed interface Device {

    /**
     * Represents a phone device.
     */
    data object Phone : Device

    /**
     * Represents a tablet device.
     */
    data object Tablet : Device
}
