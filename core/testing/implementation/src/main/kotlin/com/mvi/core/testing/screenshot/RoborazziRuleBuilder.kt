package com.mvi.core.testing.screenshot

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.RoborazziRule
import sergio.sastre.composable.preview.scanner.android.AndroidPreviewInfo
import sergio.sastre.composable.preview.scanner.core.preview.ComposablePreview
import java.io.File

/**
 * A factory object for constructing [RoborazziRule] instances used in UI tests.
 *
 * This builder encapsulates the complexity of configuring `RoborazziRule`, including:
 * - Automatic detection of the root view via [ComposeTestRule].
 * - Standardized capture modes (e.g., `LastImage`).
 * - Integration with [RoborazziOptionsBuilder] for threshold settings.
 * - Automated filename generation through [PreviewFileNameBuilder].
 *
 * @see RoborazziRule
 * @see RoborazziOptionsBuilder
 */
object RoborazziRuleBuilder {

    /**
     * Constructs a [RoborazziRule] configured for automated screenshot testing of
     * Composable previews.
     *
     * @param testRule The standard Compose test rule used to interact with the UI.
     * @param preview The metadata and content of the preview to be captured.
     * @return A fully configured [RoborazziRule] ready for use in an `@Test` method.
     */
    fun create(
        testRule: ComposeTestRule,
        preview: ComposablePreview<AndroidPreviewInfo>,
    ): RoborazziRule =
        RoborazziRule(
            composeRule = testRule,
            captureRoot = testRule.onRoot(),
            options = RoborazziRule.Options(
                captureType = RoborazziRule.CaptureType.LastImage(),
                roborazziOptions = RoborazziOptionsBuilder.create(),
                outputFileProvider = { _, dir, _ ->
                    File(dir, PreviewFileNameBuilder.create(preview))
                },
            ),
        )
}
