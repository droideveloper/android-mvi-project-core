package com.mvi.core.testing.screenshot

import sergio.sastre.composable.preview.scanner.android.AndroidComposablePreviewScanner
import sergio.sastre.composable.preview.scanner.android.AndroidPreviewInfo
import sergio.sastre.composable.preview.scanner.core.preview.ComposablePreview

/**
 * A service provider that discovers and gathers all [ComposablePreview] instances
 * within a specific package via the `AndroidComposablePreviewScanner`.
 *
 * This object implements an internal caching mechanism to ensure that the scan
 * operation (which involves reflection/tree traversal) is only performed once
 * during the test execution lifecycle.
 */
object PreviewProvider {

    private var previews: List<ComposablePreview<AndroidPreviewInfo>> = emptyList()

    /**
     * Retrieves the list of available previews for a given package.
     *
     * If this is the first call, it initiates a scan of the [packageName]
     * and includes private previews by default.
     *
     * @param packageName The full package name to scan (e.g., "com.mvi.core").
     * @return A list of discovered [ComposablePreview] objects.
     */
    fun get(packageName: String): List<ComposablePreview<AndroidPreviewInfo>> =
        when {
            previews.isEmpty() ->
                AndroidComposablePreviewScanner()
                    .scanPackageTrees(packageName)
                    .includePrivatePreviews()
                    .getPreviews().also {
                        previews = it
                    }
            else -> previews
        }
}
