package com.mvi.core.testing

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.robolectric.annotation.Config

/**
 * Base test class for Compose UI testing with Robolectric.
 *
 * This class provides a convenient way to run Compose tests against multiple
 * SDK versions. It sets up the Compose content rule and provides a helper
 * [setScreen] extension function to easily set the test screen.
 *
 * ## Usage
 * ```kotlin
 * class MyComposableTest : AbsAndroidUnitTest() {
 *
 *     @Test
 *     fun testMyComposable() {
 *         with(testRule) {
 *              setScreen {
 *                  MyComposable()
 *              }
 *
 *              onNodeWithText("Hello").isPresent
 *         }
 *     }
 * }
 * ```
 *
 * ## SDK Coverage
 * Tests are configured to run against API 26 (Android 8.0 N) by default.
 * Add additional SDK versions to the [Config.sdk] annotation as needed.
 *
 * @param sdk The Android SDK versions to test against
 * @see ComposeContentTestRule
 * @see ComposeTestRule
 */
@Config(
    sdk = [
        Build.VERSION_CODES.N,
    ],
)
abstract class AbsAndroidUnitTest {

    /**
     * The Compose content test rule for running Compose UI tests.
     *
     * Override this in subclasses to use a different rule implementation
     * or to provide a custom rule configuration.
     */
    @get:Rule
    val testRule: ComposeTestRule = createAndroidComposeRule<ComponentActivity>()

    /**
     * Convenience extension function to set the Compose content on the test rule.
     *
     * This function automatically handles the different types of compose rules
     * and applies the content to the appropriate rule implementation.
     *
     * @param content The Composable UI to set as the test screen
     */
    fun ComposeTestRule.setScreen(
        content: @Composable () -> Unit,
    ) {
        if (this is ComposeContentTestRule) {
            setContent { content() }
        }
    }
}
