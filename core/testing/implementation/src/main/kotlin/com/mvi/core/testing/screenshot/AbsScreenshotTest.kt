package com.mvi.core.testing.screenshot

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.RoborazziRule
import org.junit.Rule
import org.junit.Test
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Base class for all screenshot tests in the module.
 *
 * This class encapsulates the boilerplate configuration required to run [Roborazzi]
 * snapshots with Jetpack Compose. It integrates the [ComposeContentTestRule] and
 * [RoborazziRule] into a single unified test flow.
 *
 * ## Usage
 * ```kotin
 * @RunWith(ParameterizedRobolectricTestRunner::class)
 * class XyzScreenshotTest(suite: ScreenshotTestSuite) : AbsScreenshotTest(suite) {
 *
 *     companion object {
 *
 *         private val previewProvider = PreviewProvider(packageName = "com.mvi.core")
 *
 *         @JvmStatic
 *         @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
 *         fun values(): List<ScreenshotTestSuite> =
 *             previewProvider
 *                 .get()
 *                 .map { preview ->
 *                     ScreenshotTestSuite(
 *                         preview = preview,
 *                         name = preview.methodName,
 *                     )
 *                 }
 *     }
 * }
 * ```
 *
 * @property suite The [ScreenshotTestSuite] containing the preview content,
 * metadata, and naming conventions for the specific snapshot.
 */
@Config(
    sdk = [
        Build.VERSION_CODES.TIRAMISU,
    ],
    qualifiers = RobolectricDeviceQualifiers.Pixel5,
)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
abstract class AbsScreenshotTest(
    private val suite: ScreenshotTestSuite,
) {

    /** Rule for handling Compose UI interaction and rendering. */
    @get:Rule
    val testRule: ComposeContentTestRule = createAndroidComposeRule<ComponentActivity>()

    /**
     * Roborazzi rule that captures the screen and compares it against reference images.
     * It uses [suite.preview] to determine the content of the screenshot.
     */
    @get:Rule
    val roborazziRule: RoborazziRule =
        RoborazziRuleBuilder.create(
            testRule = testRule,
            preview = suite.preview,
        )

    /**
     * The primary test method that renders the UI content into the [testRule]
     * and triggers the Roborazzi snapshot mechanism.
     */
    @Test
    fun snapshot() {
        with(testRule) {
            setContent {
                suite.preview()
            }
        }
    }
}
