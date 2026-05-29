package com.mvi.core.repository

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * Sealed interface defining the freshness rules for cached data retrieval.
 *
 * Instances of this interface are used to determine whether a cached value
 * stored in a [com.mvi.core.repository.Cache] is considered valid for use.
 *
 * @property criteria Defines the specific freshness requirement for the cache.
 */
sealed interface Criteria {

    /**
     * Sub-interface representing criteria based on a maximum time age.
     *
     * All implementations in this branch enforce a time-based validity check
     * against the cached value's insertion time.
     */
    interface Timed : Criteria {
        /**
         * The maximum duration a cached value can exist before being considered invalid.
         *
         * @property maxAge The threshold duration for validity checks.
         */
        val maxAge: Duration
    }

    /**
     * Criterion that accepts only values that have not aged past zero.
     *
     * Effectively checks for the most immediate value available.
     * Any value that has existed for more than zero duration will fail validation.
     */
    data object Fresh : Timed {
        override val maxAge: Duration
            get() = Duration.ZERO
    }

    /**
     * Criterion that accepts values from the last 5 minutes.
     *
     * Intended for scenarios where slightly stale but recent data is preferable
     * to no data or a full re-fetch.
     */
    data object Recent : Timed {
        override val maxAge: Duration
            get() = 5.minutes
    }

    /**
     * Criterion that accepts values indefinitely until explicitly cleared.
     *
     * Intended for data that should be cached forever or until manually removed.
     */
    data object Stale : Timed {
        override val maxAge: Duration
            get() = Duration.INFINITE
    }

    companion object {
        /**
         * Factory companion object for creating custom [Timed] criteria.
         *
         * Creates a [Timed] implementation instance that will be evaluated
         * against the provided [duration] value during retrieval.
         *
         * @param duration The maximum age a cached value can have before rejection.
         * @return A [Timed] instance configured with the specified duration.
         * @see equals The equality logic for these custom instances compares [maxAge].
         */
        fun ofTimed(duration: Duration): Timed = object : Timed {
            override val maxAge: Duration
                get() = duration

            override fun equals(other: Any?): Boolean {
                if (other is Timed) {
                    return maxAge == other.maxAge
                }
                return false
            }
        }
    }
}
