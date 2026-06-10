package com.mvi.core.testing.screenshot

import sergio.sastre.composable.preview.scanner.android.AndroidPreviewInfo
import sergio.sastre.composable.preview.scanner.core.preview.ComposablePreview

/**
 * A data container representing a single unit of work for the screenshot testing suite.
 *
 * Each instance maps a [ComposablePreview] (the UI component to be captured)
 * to a human-readable [name] used for identification in test reports.
 *
 * @property preview The actual Compose Preview content found by the scanner.
 * @property name The display name of this specific preview, typically derived from the method name.
 */
data class ScreenshotTestSuite(
    val preview: ComposablePreview<AndroidPreviewInfo>,
    val name: String,
) {
    /**
     * Returns the descriptive name of the test suite.
     */
    override fun toString(): String = name
}
