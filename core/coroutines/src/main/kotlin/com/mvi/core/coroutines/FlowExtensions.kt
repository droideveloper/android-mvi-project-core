@file:OptIn(ExperimentalTime::class)

package com.mvi.core.coroutines

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Throttles a [Flow] by emitting values only after the specified delay has passed
 * since the last emission.
 *
 * The first value is always emitted immediately. Subsequent values are only emitted
 * if at least [delay] time has elapsed since the previous emission.
 *
 * @param delay The minimum time interval between consecutive emissions. Must be positive.
 * @return A new [Flow] that emits throttled values.
 *
 */
@OptIn(ExperimentalTime::class)
fun <T> Flow<T>.throttle(delay: Duration): Flow<T> = flow {
    require(delay.isPositive()) { "Delay must be positive" }
    var previous: Instant? = null
    collect { value ->
        val current = Clock.System.now()
        when (val before = previous) {
            null -> {
                previous = current
                emit(value)
            }
            else -> if (current.minus(before) >= delay) {
                previous = current
                emit(value)
            }
        }
    }
}
