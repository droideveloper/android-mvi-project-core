package com.mvi.core.location

import kotlinx.coroutines.flow.Flow

interface LocationProvider {
    fun requestUserLocation(): Flow<Result<UserLocation>>
}
