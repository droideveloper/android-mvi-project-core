package com.mvi.core.location

data class UserLocation(
    val lat: Double,
    val lon: Double,
) {

    companion object {
        val Default = UserLocation(
            lat = 37.7749,
            lon = -122.4194,
        )
    }
}
