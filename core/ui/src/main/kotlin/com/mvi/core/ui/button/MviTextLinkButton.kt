package com.mvi.core.ui.button

import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import com.mvi.core.ui.MviTheme

/**
 * Text link button component styled as an underlined text.
 *
 * This button appears as plain text with an underline, suitable for actions like "Login"
 * or "Read more". It uses the theme's primary blue color and supports pressed/pressed states.
 *
 * @param text The text to display (appears as underlined text)
 * @param onClick Callback invoked when the button is clicked
 * @param modifier The modifier to apply to the button
 * @param enabled Whether the button is enabled and responds to clicks
 * @param textColor Custom text color for the button (default: primary blue)
 * @param pressedTextColor Custom text color when pressed (default: secondary blue)
 * @param textStyle Custom text style for the button (default: body secondary with underline)
 * @param indication Custom ripple/indication to apply
 */
@Composable
fun MviTextLinkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textColor: Color = MviTheme.colors.blues.primary,
    pressedTextColor: Color = MviTheme.colors.blues.secondary,
    textStyle: TextStyle = MviTheme.typography.bodySecondary,
    indication: Indication? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressedState = interactionSource.collectIsPressedAsState()

    Text(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = indication,
                onClick = if (enabled) onClick else ({}),
            )
            .testTag("button_text")
            .then(modifier),
        text = text,
        color = if (enabled) {
            if (pressedState.value) {
                pressedTextColor
            } else {
                textColor
            }
        } else {
            MviTheme.colors.greys.primary
        },
        style = textStyle.copy(textDecoration = TextDecoration.Underline),
    )
}
