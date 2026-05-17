package com.mvi.core.environment

sealed interface Device {

    data object Phone : Device
    data object Tablet : Device
}
