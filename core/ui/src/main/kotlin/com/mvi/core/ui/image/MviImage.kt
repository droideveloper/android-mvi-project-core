package com.mvi.core.ui.image

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics

val DrawableResource = SemanticsPropertyKey<ImageVector>("ImageVector")
var SemanticsPropertyReceiver.drawableResource by DrawableResource

/**
 * Image component for displaying vector images.
 *
 * This component displays an [ImageVector] with optional content description for accessibility.
 * It supports custom content scaling and color filtering for flexible image rendering.
 *
 * @param resource The image vector to display
 * @param modifier The modifier to apply to the image
 * @param contentDescription Accessibility description for the image (required for non-decorative images)
 * @param contentScale How to scale the image when the aspect ratio of the bounds differs from the image
 * @param colorFilter Optional color filter to apply to the image
 */
@Composable
fun MviImage(
    resource: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit,
    colorFilter: ColorFilter? = null,
) {
    Image(
        painter = rememberVectorPainter(resource),
        modifier = modifier.semantics { drawableResource = resource },
        contentDescription = contentDescription,
        contentScale = contentScale,
        colorFilter = colorFilter,
    )
}
