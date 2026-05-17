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
