package com.mvi.core.app

import kotlinx.coroutines.flow.Flow

interface OrientationProvider {

    suspend fun setCurrentOrientation(orientation: Int)

    operator fun invoke(): Flow<Int>
}
