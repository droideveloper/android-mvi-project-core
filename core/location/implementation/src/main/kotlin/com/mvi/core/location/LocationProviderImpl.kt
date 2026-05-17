@file:SuppressLint("MissingPermission") // provided in runtime

package com.mvi.core.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LocationProviderImpl @Inject constructor(
    private val contextProvider: () -> Context,
    private val locationRequest: LocationRequest,
) : LocationProvider {

    override fun requestUserLocation(): Flow<Result<UserLocation>> =
        channelFlow {
            val locationClient = LocationServices.getFusedLocationProviderClient(contextProvider())
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val last = result.locations.last() //  only take latest not interested in update
                    trySend(last)
                }
            }
            locationClient.lastLocation.await<Location?>()?.let { send(it) }
            locationClient.requestLocationUpdates(locationRequest, locationCallback, null).await()
            awaitClose {
                locationClient.removeLocationUpdates(locationCallback)
            }
        }
        .map {
            Result.success(
                value = UserLocation(
                    lat = it.latitude,
                    lon = it.longitude,
                )
            )
        }
        .catch { Result.failure<UserLocation>(exception = it) }
}

internal object MockLocationProvider : LocationProvider {
    override fun requestUserLocation(): Flow<Result<UserLocation>> = flow {
        // San Francisco as mock
        emit(
            value = Result.success(
                value = UserLocation(
                    lat = 37.7749,
                    lon = -122.4194,
                )
            )
        )
    }
}
