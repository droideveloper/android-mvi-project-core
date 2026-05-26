package com.mvi.core.environment

import javax.inject.Inject

/**
 * Default implementation of [Environment] that reads from [BuildConfig].
 */
class EnvironmentImpl @Inject constructor() : Environment {

    override val flavorName: String
        get() = BuildConfig.FLAVOR

    override val isDebug: Boolean
        get() = BuildConfig.DEBUG

    override val isRelease: Boolean
        get() = BuildConfig.DEBUG.not()
}
