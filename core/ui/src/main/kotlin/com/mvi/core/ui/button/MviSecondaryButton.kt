package com.mvi.core.ui.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults.outlinedButtonColors
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.mvi.core.ui.MviTheme

/**
 * Secondary button component with outlined border and transparent background.
 *
 * This button displays white text on a transparent background with a white border,
 * suitable for secondary actions or less prominent CTAs. It supports loading state and
 * enabled/disabled states.
 *
 * @param text The text to display on the button
 * @param onClick Callback invoked when the button is clicked (only if enabled and not loading)
 * @param modifier The modifier to apply to the button
 * @param enabled Whether the button is enabled and responds to clicks
 * @param loading Whether the button is in a loading state (shows spinner instead of text)
 * @param iconResource Optional icon resource to display alongside the text
 * @param borderColor Custom border color for the button (default: primary white)
 * @param contentColor Custom text color for the button (default: primary white)
 */
@Composable
fun MviSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    iconResource: ImageVector? = null,
    contentColor: Color = MviTheme.colors.blacks.primary,
) {
    OutlinedButton(
        onClick = if (enabled && !loading) onClick else ({}),
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .height(MviTheme.dimens.standard48),
        colors = outlinedButtonColors(
            contentColor = contentColor,
            disabledContentColor = MviTheme.colors.greys.primary,
        ),
        border = BorderStroke(
            width = MviTheme.dimens.standard1,
            color = if (enabled) contentColor else MviTheme.colors.greys.primary,
        ),
        contentPadding = PaddingValues(
            vertical = MviTheme.dimens.standard0,
            horizontal = MviTheme.dimens.standard16,
        ),
    ) {
        if (loading) {
            MviButtonLoadingIndicator(color = contentColor)
        } else {
            if (iconResource != null) {
                MviButtonIcon(iconResource = iconResource, iconSize = MviTheme.dimens.standard20)
            }
            MviButtonText(
                text = text,
                textStyle = MviTheme.typography.bodyPrimary,
                color = if (enabled) contentColor else MviTheme.colors.greys.primary,
            )
        }
    }
}
