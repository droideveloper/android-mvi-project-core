package com.mvi.core.ui.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.mvi.core.ui.MviTheme

/**
 * Loading indicator component for buttons.
 *
 * Displays a circular progress indicator when the button is in a loading state.
 * Uses the primary loading color and standard button icon size.
 *
 * @param color The color to use for the loading indicator (default: primary loading color)
 */
@Composable
fun MviButtonLoadingIndicator(color: Color) {
    CircularProgressIndicator(
        color = color,
        strokeWidth = MviTheme.dimens.standard2,
        modifier = Modifier.Companion
            .height(MviTheme.dimens.standard32)
            .width(MviTheme.dimens.standard32)
            .testTag("button_loading"),
    )
}

/**
 * Button icon component.
 *
 * Displays an icon in the button with optional padding to separate it from the text.
 * Used for action icons like arrows, refresh, etc.
 *
 * @param iconResource The image vector to display as the icon
 * @param iconSize The size of the icon
 */
@Composable
internal fun MviButtonIcon(
    iconResource: ImageVector,
    iconSize: Dp,
) {
    Box(modifier = Modifier.padding(end = MviTheme.dimens.standard8)) {
        Icon(
            painter = rememberVectorPainter(iconResource),
            contentDescription = null,
            modifier = Modifier
                .size(iconSize)
                .testTag("button_icon"),
        )
    }
}

/**
 * Button text component.
 *
 * Displays the text content of the button centered in the button.
 * Applies the provided text style and color.
 *
 * @param text The text to display
 * @param textStyle The text style to apply
 * @param color The color of the text
 */
@Composable
internal fun MviButtonText(
    text: String,
    textStyle: TextStyle,
    color: Color,
) {
    Text(
        text = text,
        style = textStyle,
        color = color,
        textAlign = TextAlign.Center,
        modifier = Modifier.testTag("button_text"),
    )
}
