package com.mvi.core.location

/**
 * Data class representing a user's geolocation.
 *
 * Contains latitude and longitude coordinates to specify a precise
 * position on the Earth's surface. This class is used throughout the
 * location subsystem to carry location information between layers.
 *
 * @property lat The latitude coordinate, ranging from -90.0 (South Pole)
 *               to 90.0 (North Pole) in decimal degrees
 * @property lon The longitude coordinate, ranging from -180.0 to 180.0
 *               in decimal degrees, with 0.0 at the Prime Meridian
 */
data class UserLocation(
    /**
     * The latitude coordinate in decimal degrees.
     *
     * Valid range: -90.0 (South Pole) to 90.0 (North Pole)
     */
    val lat: Double,
    /**
     * The longitude coordinate in decimal degrees.
     *
     * Valid range: -180.0 to 180.0, with 0.0 at the Prime Meridian
     */
    val lon: Double,
) {

    companion object {
        /**
         * Default location for fallback scenarios.
         *
         * Represents San Francisco, California as a fallback location when
         * actual location data is unavailable or not yet acquired.
         *
         * @property lat 37.7749 - San Francisco latitude
         * @property lon -122.4194 - San Francisco longitude
         */
        val Default = UserLocation(
            lat = 37.7749,
            lon = -122.4194,
        )
    }
}
