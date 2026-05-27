package com.mvi.core.ui.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.mvi.core.ui.MviTheme

/**
 * Primary button component with solid background color and optional icon.
 *
 * This button uses the theme's primary black color as its background and white text.
 * It supports loading state, enabled/disabled states, and optional icon display.
 *
 * @param text The text to display on the button
 * @param onClick Callback invoked when the button is clicked (only if enabled and not loading)
 * @param modifier The modifier to apply to the button
 * @param enabled Whether the button is enabled and responds to clicks
 * @param loading Whether the button is in a loading state (shows spinner instead of text)
 * @param iconResource Optional icon resource to display alongside the text
 * @param backgroundColor Custom background color for the button (default: primary black)
 * @param contentColor Custom text color for the button (default: primary white)
 * @param border Optional border stroke to apply to the button
 */
@Composable
fun MviPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    iconResource: ImageVector? = null,
    backgroundColor: Color = MviTheme.colors.blacks.primary,
    contentColor: Color = MviTheme.colors.whites.primary,
    border: BorderStroke? = null,
) {
    Button(
        onClick = if (enabled && !loading) onClick else ({}),
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .height(MviTheme.dimens.standard48),
        colors = buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            disabledContainerColor = MviTheme.colors.greys.primary,
            disabledContentColor = MviTheme.colors.whites.primary,
        ),
        contentPadding = PaddingValues(
            vertical = MviTheme.dimens.standard0,
            horizontal = MviTheme.dimens.standard16,
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = MviTheme.dimens.standard0,
            pressedElevation = MviTheme.dimens.standard0,
            disabledElevation = MviTheme.dimens.standard0,
            focusedElevation = MviTheme.dimens.standard0,
            hoveredElevation = MviTheme.dimens.standard0,
        ),
        border = border,
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
                color = contentColor,
            )
        }
    }
}
