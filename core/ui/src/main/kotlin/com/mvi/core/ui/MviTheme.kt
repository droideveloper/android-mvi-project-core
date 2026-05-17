package com.mvi.core.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mvi.core.environment.Device
import com.mvi.core.environment.R

val LocalMviColors = staticCompositionLocalOf { mviColors }
val LocalMviDimens = staticCompositionLocalOf { mviDimens }
val LocalMviTypography = staticCompositionLocalOf { mviTypography }

val LocalMviDevice = staticCompositionLocalOf<Device> { Device.Phone }

@Composable
fun MviTheme(content: @Composable () -> Unit) {
    val isTablet = booleanResource(R.bool.isTablet)
    val device: Device = remember(isTablet) {
        when {
            isTablet -> Device.Tablet
            else -> Device.Phone
        }
    }
    CompositionLocalProvider(
        LocalMviColors provides mviColors,
        LocalMviDimens provides mviDimens,
        LocalMviTypography provides { mviTypography() },
        LocalMviDevice provides device,
    ) {
        MaterialTheme(
            colorScheme = lightColorScheme(
                primary = MviTheme.colors.blues.primary,
                background = MviTheme.colors.whites.primary,
            ),
            shapes = Shapes(
                small = RoundedCornerShape(MviTheme.dimens.standard72),
            ),
            content = content,
        )
    }
}

object MviTheme {
    val colors: MviColors
        @Composable
        get() = LocalMviColors.current
    val dimens: MviDimens
        @Composable
        get() = LocalMviDimens.current
    val typography: MviTypography
        @Composable
        get() = LocalMviTypography.current()

    val device: Device
        @Composable
        get() = LocalMviDevice.current
}

@Immutable
data class MviDimens(
    val standard0: Dp,
    val standard1: Dp,
    val standard2: Dp,
    val standard4: Dp,
    val standard6: Dp,
    val standard8: Dp,
    val standard10: Dp,
    val standard12: Dp,
    val standard16: Dp,
    val standard18: Dp,
    val standard20: Dp,
    val standard24: Dp,
    val standard28: Dp,
    val standard30: Dp,
    val standard32: Dp,
    val standard36: Dp,
    val standard40: Dp,
    val standard42: Dp,
    val standard48: Dp,
    val standard54: Dp,
    val standard60: Dp,
    val standard64: Dp,
    val standard72: Dp,
    val standard96: Dp,
    val standard108: Dp,
    val standard128: Dp,
    val standard148: Dp,
    val standard164: Dp,
    val standard196: Dp,
    val standard256: Dp,
    val standard320: Dp,
    val standard480: Dp,
)

val mviDimens = MviDimens(
    standard0 = 0.dp,
    standard1 = 1.dp,
    standard2 = 2.dp,
    standard4 = 4.dp,
    standard6 = 6.dp,
    standard8 = 8.dp,
    standard10 = 10.dp,
    standard12 = 12.dp,
    standard16 = 16.dp,
    standard18 = 18.dp,
    standard20 = 20.dp,
    standard24 = 24.dp,
    standard28 = 28.dp,
    standard30 = 30.dp,
    standard32 = 32.dp,
    standard36 = 36.dp,
    standard40 = 40.dp,
    standard42 = 42.dp,
    standard48 = 48.dp,
    standard54 = 54.dp,
    standard60 = 60.dp,
    standard64 = 64.dp,
    standard72 = 72.dp,
    standard96 = 96.dp,
    standard108 = 108.dp,
    standard128 = 128.dp,
    standard148 = 148.dp,
    standard164 = 164.dp,
    standard196 = 196.dp,
    standard256 = 256.dp,
    standard320 = 320.dp,
    standard480 = 480.dp,
)

@Immutable
data class MviColor(
    val primary: Color,
    val secondary: Color,
    val light: Color,
)

@Immutable
data class MviColors(
    val reds: MviColor,
    val pinks: MviColor,
    val purples: MviColor,
    val deepPurples: MviColor,
    val indigos: MviColor,
    val blues: MviColor,
    val lightBlues: MviColor,
    val cyans: MviColor,
    val teals: MviColor,
    val greens: MviColor,
    val lightGreens: MviColor,
    val limes: MviColor,
    val yellows: MviColor,
    val ambers: MviColor,
    val oranges: MviColor,
    val deepOranges: MviColor,
    val browns: MviColor,
    val greys: MviColor,
    val blueGreys: MviColor,
    val blacks: MviColor,
    val whites: MviColor,
)

private val mviColors = MviColors(
    reds = MviColor(
        primary = Color(0xFFF44336),
        secondary = Color(0xFFEF5350),
        light = Color(0xFFE57373),
    ),
    pinks = MviColor(
        primary = Color(0xFFE91E63),
        secondary = Color(0xFFEC407A),
        light = Color(0xFFF06292),
    ),
    purples = MviColor(
        primary = Color(0xFF9C27B0),
        secondary = Color(0xFFAB47BC),
        light = Color(0xFFBA68C8),
    ),
    deepPurples = MviColor(
        primary = Color(0xFF673AB7),
        secondary = Color(0xFF7E57C2),
        light = Color(0xFF9575CD),
    ),
    indigos = MviColor(
        primary = Color(0xFF3F51B5),
        secondary = Color(0xFF5C6BC0),
        light = Color(0xFF7986CB),
    ),
    blues = MviColor(
        primary = Color(0xFF2196F3),
        secondary = Color(0xFF42A5F5),
        light = Color(0xFF64B5F6),
    ),
    lightBlues = MviColor(
        primary = Color(0xFF03A9F4),
        secondary = Color(0xFF29B6F6),
        light = Color(0xFF4FC3F7),
    ),
    cyans = MviColor(
        primary = Color(0xFF00BCD4),
        secondary = Color(0xFF26C6DA),
        light = Color(0xFF4DD0E1),
    ),
    teals = MviColor(
        primary = Color(0xFF009688),
        secondary = Color(0xFF26A69A),
        light = Color(0xFF4DB6AC),
    ),
    greens = MviColor(
        primary = Color(0xFF4CAF50),
        secondary = Color(0xFF66BB6A),
        light = Color(0xFF81C784),
    ),
    lightGreens = MviColor(
        primary = Color(0xFF8BC34A),
        secondary = Color(0xFF9CCC65),
        light = Color(0xFFAED581),
    ),
    limes = MviColor(
        primary = Color(0xFFCDDC39),
        secondary = Color(0xFFD4E157),
        light = Color(0xFFDCE775),
    ),
    yellows = MviColor(
        primary = Color(0xFFFFEB3B),
        secondary = Color(0xFFFFEE58),
        light = Color(0xFFFFF176),
    ),
    ambers = MviColor(
        primary = Color(0xFFFFC107),
        secondary = Color(0xFFFFCA28),
        light = Color(0xFFFFD54F),
    ),
    oranges = MviColor(
        primary = Color(0xFFFF9800),
        secondary = Color(0xFFFFA726),
        light = Color(0xFFFFB74D),
    ),
    deepOranges = MviColor(
        primary = Color(0xFFFF5722),
        secondary = Color(0xFFFF7043),
        light = Color(0xFFFF8A65),
    ),
    browns = MviColor(
        primary = Color(0xFF795548),
        secondary = Color(0xFF8D6E63),
        light = Color(0xFFA1887F),
    ),
    greys = MviColor(
        primary = Color(0xFF9E9E9E),
        secondary = Color(0xFFBDBDBD),
        light = Color(0xFFE0E0E0),
    ),
    blueGreys = MviColor(
        primary = Color(0xFF607D8B),
        secondary = Color(0xFF78909C),
        light = Color(0xFF90A4AE),
    ),
    blacks = MviColor(
        primary = Color(0xFF1E1F26),
        secondary = Color(0xEE3F4050),
        light = Color(0xCC3F4050),
    ),
    whites = MviColor(
        primary = Color(0xFFFFFFFF),
        secondary = Color(0xFFF7F7FA),
        light = Color(0xFFF2F2F5),
    ),
)

@Immutable
data class MviTypography(
    val titlePrimary: TextStyle,
    val titleSecondary: TextStyle,
    val bodyPrimary: TextStyle,
    val bodySecondary: TextStyle,
    val spotPrimary: TextStyle,
    val spotSecondary: TextStyle,
)

private val mviTypography
    get() =
        @Composable {
        val isTablet = LocalMviDevice.current == Device.Tablet
        MviTypography(
            titlePrimary = TextStyle.Default.copy(
                fontSize = if (isTablet) 38.sp else 19.sp,
                color = MviTheme.colors.blacks.primary,
            ),
            titleSecondary = TextStyle.Default.copy(
                fontSize = if (isTablet) 32.sp else 16.sp,
                color = MviTheme.colors.greys.primary,
            ),
            bodyPrimary = TextStyle.Default.copy(
                fontSize = if (isTablet) 28.sp else 14.sp,
                color = MviTheme.colors.greys.primary,
            ),
            bodySecondary = TextStyle.Default.copy(
                fontSize = if (isTablet) 24.sp else 12.sp,
                color = MviTheme.colors.greys.secondary,
            ),
            spotPrimary = TextStyle.Default.copy(
                fontSize = if (isTablet) 20.sp else 10.sp,
                color = MviTheme.colors.greys.secondary,
            ),
            spotSecondary = TextStyle.Default.copy(
                fontSize = if (isTablet) 16.sp else 8.sp,
                color = MviTheme.colors.blacks.primary,
            ),
        )
    }

fun Color?.default(defaultColor: Color): Color {
    return if (this == null || this == Color.Unspecified) {
        defaultColor
    } else {
        this
    }
}
