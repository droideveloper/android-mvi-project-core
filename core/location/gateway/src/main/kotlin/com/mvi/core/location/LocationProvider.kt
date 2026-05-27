package com.mvi.core.location

import kotlinx.coroutines.flow.Flow

/**
 * Interface for providing user location data.
 *
 * This provider encapsulates location retrieval logic and exposes a
 * reactive stream of location updates through [Flow]. Consumers can
 * observe location changes and handle success or failure cases via
 * the [Result] wrapper.
 *
 * @see UserLocation
 */
interface LocationProvider {
    /**
     * Requests and streams the current user's location.
     *
     * This function initiates a location request and returns a [Flow]
     * of [Result][Result.success]/[Result.failure] containing [UserLocation].
     * Each emission represents either a new location update or an error
     * that occurred during location retrieval.
     *
     * Emissions occur:
     * - On successful location acquisition (success emission)
     * - When location permission is denied (failure emission)
     * - When location services are unavailable (failure emission)
     * - On location update events (success emission with new location)
     *
     * @return A [Flow] emitting [Result] instances containing the current
     * location or any error that occurred during the request
     *
     * @see UserLocation
     */
    fun requestUserLocation(): Flow<Result<UserLocation>>
}
