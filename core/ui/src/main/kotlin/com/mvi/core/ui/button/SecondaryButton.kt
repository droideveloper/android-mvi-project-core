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
