package com.mvi.core.testing.screenshot

import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.ThresholdValidator

/**
 * A builder utility for constructing [RoborazziOptions] configurations.
 *
 * This object abstracts the boilerplate required to set up Roborazzi's comparison logic,
 * specifically focusing on providing a simplified interface for defining image
 * variance thresholds.
 *
 * @see RoborazziOptions
 * @see ThresholdValidator
 */
object RoborazziOptionsBuilder {

    /**
     * The default tolerance for pixel difference.
     * A value of `0.01f` represents a 1% allowed variance before a test fails.
     */
    private const val DefaultMaxDifferencePercentage = 0.01f

    /**
     * Creates a [RoborazziOptions] instance with specified comparison thresholds.
     *
     * @param maxDifferencePercentage The maximum allowed difference between the baseline
     * and current image as a float (e.g., `0.05f` for 5%). Defaults to 1%.
     * @return A configured [RoborazziOptions] object ready for use in Roborazzi tests.
     */
    fun create(
        maxDifferencePercentage: Float = DefaultMaxDifferencePercentage,
    ): RoborazziOptions =
        RoborazziOptions(
            compareOptions = RoborazziOptions.CompareOptions(
                resultValidator = ThresholdValidator(maxDifferencePercentage),
            ),
        )
}
