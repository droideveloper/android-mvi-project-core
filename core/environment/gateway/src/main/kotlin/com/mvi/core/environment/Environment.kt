package com.mvi.core.environment
/**
 * Provides runtime environment context including:
 * - Environment flavor (debug, mock, staging, prod)
 * - Device type detection (phone, tablet)
 */
interface Environment {

    /**
     * The name of the current flavor build (e.g., "debug", "mock", "staging", "prod").
     */
    val flavorName: String

    /**
     * Whether the app is running in a debug build.
     */
    val isDebug: Boolean

    /**
     * Whether the app is running in a release build.
     */
    val isRelease: Boolean

    /**
     * Whether the app is running in the mock flavor.
     */
    val isMock: Boolean
        get() = flavorName == "mock"

    /**
     * Whether the app is running in the staging flavor.
     */
    val isStaging: Boolean
        get() = flavorName == "staging"

    /**
     * Whether the app is running in the production flavor.
     */
    val isProd: Boolean
        get() = flavorName == "prod"
}
