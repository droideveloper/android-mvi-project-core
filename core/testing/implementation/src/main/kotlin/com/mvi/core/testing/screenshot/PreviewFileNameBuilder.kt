package com.mvi.core.testing.screenshot

import sergio.sastre.composable.preview.scanner.android.AndroidPreviewInfo
import sergio.sastre.composable.preview.scanner.android.screenshotid.AndroidPreviewScreenshotIdBuilder
import sergio.sastre.composable.preview.scanner.core.preview.ComposablePreview

/**
 * Utility object responsible for generating deterministic file names for screenshot outputs.
 *
 * This ensures that every [ComposablePreview] is mapped to a consistent filename
 * based on its internal identifiers, preventing collisions and ensuring consistency
 * across CI/CD pipelines.
 */
object PreviewFileNameBuilder {

    /**
     * Constructs a .png filename for a given [ComposablePreview].
     *
     * @param preview The preview content to derive the name from.
     * @return A string representing the filename (e.g., "MyComponent_State_SomeID.png").
     */
    fun create(preview: ComposablePreview<AndroidPreviewInfo>): String =
        "${AndroidPreviewScreenshotIdBuilder(preview).build()}.png"
}
