package com.mvi.core.environment

interface Environment {

    val flavorName: String

    val isDebug: Boolean

    val isRelease: Boolean

    val isMock: Boolean
        get() = flavorName == "mock"

    val isStaging: Boolean
        get() = flavorName == "staging"

    val isProd: Boolean
        get() = flavorName == "prod"
}
