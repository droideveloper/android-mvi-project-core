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

/**
 * CompositionLocal providing the application's color scheme.
 *
 * Access via [LocalMviColors].
 *
 * @property reds Red color palette (error states)
 * @property pinks Pink color palette
 * @property purples Purple color palette
 * @property deepPurples Deep purple color palette
 * @property indigos Indigo color palette
 * @property blues Blue color palette (primary brand color)
 * @property lightBlues Light blue color palette
 * @property cyans Cyan color palette
 * @property teals Teal color palette
 * @property greens Green color palette (success states)
 * @property lightGreens Light green color palette
 * @property limes Lime color palette
 * @property yellows Yellow color palette (warnings)
 * @property ambers Amber color palette
 * @property oranges Orange color palette
 * @property deepOranges Deep orange color palette
 * @property browns Brown color palette
 * @property greys Grey color palette
 * @property blueGreys Blue-grey color palette
 * @property blacks Black color palette (text on light backgrounds)
 * @property whites White color palette (backgrounds)
 */
val LocalMviColors = staticCompositionLocalOf { mviColors }

/**
 * CompositionLocal providing dimension values for the application.
 *
 * Access via [LocalMviDimens]. Contains standard spacing and sizing values
 * for consistent UI design across the application.
 *
 * @property standard0 Zero spacing
 * @property standard1 1dp spacing
 * @property standard2 2dp spacing
 * @property standard4 4dp spacing
 * @property standard6 6dp spacing
 * @property standard8 8dp spacing (standard spacing unit)
 * @property standard10 10dp spacing
 * @property standard12 12dp spacing
 * @property standard16 16dp spacing (standard margin)
 * @property standard18 18dp spacing
 * @property standard20 20dp spacing
 * @property standard24 24dp spacing (standard padding)
 * @property standard28 28dp spacing
 * @property standard30 30dp spacing
 * @property standard32 32dp spacing (standard card size)
 * @property standard36 36dp spacing
 * @property standard40 40dp spacing
 * @property standard42 42dp spacing
 * @property standard48 48dp spacing
 * @property standard54 54dp spacing
 * @property standard60 60dp spacing
 * @property standard64 64dp spacing
 * @property standard72 72dp spacing (rounded corner size)
 * @property standard96 96dp spacing
 * @property standard108 108dp spacing
 * @property standard128 128dp spacing
 * @property standard148 148dp spacing
 * @property standard164 164dp spacing
 * @property standard196 196dp spacing
 * @property standard256 256dp spacing
 * @property standard320 320dp spacing
 * @property standard480 480dp spacing
 */
val LocalMviDimens = staticCompositionLocalOf { mviDimens }

/**
 * CompositionLocal providing typography styles for the application.
 *
 * Access via [LocalMviTypography]. Contains predefined text styles that
 * adapt to the current device type (phone vs tablet).
 *
 * @property titlePrimary Main title style (19sp on phone, 38sp on tablet)
 * @property titleSecondary Secondary title style (16sp on phone, 32sp on tablet)
 * @property bodyPrimary Primary body text style (14sp on phone, 28sp on tablet)
 * @property bodySecondary Secondary body text style (12sp on phone, 24sp on tablet)
 * @property spotPrimary Spot/small text style (10sp on phone, 20sp on tablet)
 * @property spotSecondary Secondary spot text style (8sp on phone, 16sp on tablet)
 */
val LocalMviTypography = staticCompositionLocalOf { mviTypography }

/**
 * CompositionLocal providing the current device type.
 *
 * Access via [LocalMviDevice]. Used to adapt UI layouts and typography
 * based on whether the device is a phone or tablet.
 *
 * @see Device
 */
val LocalMviDevice = staticCompositionLocalOf<Device> { Device.Phone }

/**
 * Main theme composable that provides the application's visual design system.
 *
 * Sets up the Material 3 theme with custom colors and shapes, and provides
 * composition locals for colors, dimensions, typography, and device type.
 *
 * ## Usage
 * ```kotlin
 * MviTheme {
 *     // Your app content here
 * }
 * ```
 *
 * ## Theme Properties
 * - **Colors**: Material 3 color scheme based on blue primary and white background
 * - **Shapes**: Rounded corners with 72dp radius for small elements
 * - **Device Detection**: Automatically detects phone vs tablet layout
 *
 * @param content The composable content to display within the theme
 */
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

/**
 * Accessors for theme properties.
 *
 * @property colors [MviColors] instance containing all color palettes
 * @property dimens [MviDimens] instance containing all dimension values
 * @property typography [MviTypography] instance containing all text styles
 * @property device Current [Device] type (Phone or Tablet)
 */
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

/**
 * Immutable data class holding dimension values for consistent UI spacing.
 *
 * All values are expressed in density-independent pixels (dp) and should
 * be used for spacing, padding, margins, and sizing throughout the application.
 *
 * @property standard0 Zero spacing (0dp)
 * @property standard1 Small spacing (1dp)
 * @property standard2 Small spacing (2dp)
 * @property standard4 Small spacing (4dp)
 * @property standard6 Small spacing (6dp)
 * @property standard8 Standard spacing (8dp)
 * @property standard10 Medium spacing (10dp)
 * @property standard12 Medium spacing (12dp)
 * @property standard16 Standard margin (16dp)
 * @property standard18 Medium spacing (18dp)
 * @property standard20 Medium spacing (20dp)
 * @property standard24 Standard padding (24dp)
 * @property standard28 Large spacing (28dp)
 * @property standard30 Large spacing (30dp)
 * @property standard32 Standard card size (32dp)
 * @property standard36 Large spacing (36dp)
 * @property standard40 Large spacing (40dp)
 * @property standard42 Large spacing (42dp)
 * @property standard48 Large spacing (48dp)
 * @property standard54 Large spacing (54dp)
 * @property standard60 Large spacing (60dp)
 * @property standard64 Large spacing (64dp)
 * @property standard72 Rounded corner size (72dp)
 * @property standard96 Extra large spacing (96dp)
 * @property standard108 Extra large spacing (108dp)
 * @property standard128 Extra large spacing (128dp)
 * @property standard148 Extra large spacing (148dp)
 * @property standard164 Extra large spacing (164dp)
 * @property standard196 Extra large spacing (196dp)
 * @property standard256 Very large spacing (256dp)
 * @property standard320 Very large spacing (320dp)
 * @property standard480 Extra large spacing (480dp)
 */
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

/**
 * Instance of [MviDimens] providing all standard dimension values.
 *
 * @see MviDimens
 */
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

/**
 * Immutable data class holding a color palette with primary, secondary, and light variants.
 *
 * Each color palette follows Material Design guidelines with:
 * - [primary]: The main brand color
 * - [secondary]: A complementary or variant color
 * - [light]: A lighter version of the primary color
 *
 * @property primary The main color for the palette
 * @property secondary A variant or complementary color
 * @property light A lighter variant of the primary color
 */
@Immutable
data class MviColor(
    val primary: Color,
    val secondary: Color,
    val light: Color,
)

/**
 * Immutable data class holding all color palettes for the application.
 *
 * Contains predefined palettes for each Material Design color family:
 * - [reds]: Error states and alerts
 * - [pinks]: Pink color family
 * - [purples]: Purple color family
 * - [deepPurples]: Deep purple color family
 * - [indigos]: Indigo color family
 * - [blues]: Primary brand color (blue)
 * - [lightBlues]: Light blue color family
 * - [cyans]: Cyan color family
 * - [teals]: Teal color family
 * - [greens]: Success states
 * - [lightGreens]: Light green color family
 * - [limes]: Lime color family
 * - [yellows]: Warning states
 * - [ambers]: Amber color family
 * - [oranges]: Orange color family
 * - [deepOranges]: Deep orange color family
 * - [browns]: Brown color family
 * - [greys]: Neutral grey tones
 * - [blueGreys]: Blue-grey neutral tones
 * - [blacks]: Black/dark tones for text
 * - [whites]: White tones for backgrounds
 *
 * Usage:
 * ```kotlin
 * // Get a primary color
 * MviTheme.colors.blues.primary
 *
 * // Get a success color
 * MviTheme.colors.greens.primary
 *
 * // Get a warning color
 * MviTheme.colors.yellows.primary
 * ```
 *
 * @property reds Red color palette
 * @property pinks Pink color palette
 * @property purples Purple color palette
 * @property deepPurples Deep purple color palette
 * @property indigos Indigo color palette
 * @property blues Blue color palette (primary brand color)
 * @property lightBlues Light blue color palette
 * @property cyans Cyan color palette
 * @property teals Teal color palette
 * @property greens Green color palette
 * @property lightGreens Light green color palette
 * @property limes Lime color palette
 * @property yellows Yellow color palette
 * @property ambers Amber color palette
 * @property oranges Orange color palette
 * @property deepOranges Deep orange color palette
 * @property browns Brown color palette
 * @property greys Grey color palette
 * @property blueGreys Blue-grey color palette
 * @property blacks Black color palette
 * @property whites White color palette
 */
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

/**
 * Application-wide color configuration with all color palettes.
 *
 * This instance provides the complete color system for the application,
 * following Material Design 3 guidelines. Each palette includes primary,
 * secondary, and light variants for consistent theming.
 *
 * ## Usage
 * ```kotlin
 * // Access colors within MviTheme
 * MviTheme.colors.blues.primary
 * MviTheme.colors.greens.primary
 * MviTheme.colors.whites.secondary
 *
 * // Direct access outside theme (not recommended)
 * mviColors.blues.primary
 * ```
 *
 * ## Color Palette Reference
 * | Palette | Primary | Secondary | Light | Usage |
 * |---------|---------|-----------|-------|-------|
 * | blues | #2196F3 | #42A5F5 | #64B5F6 | Primary brand color |
 * | greens | #4CAF50 | #66BB6A | #81C784 | Success states |
 * | reds | #F44336 | #EF5350 | #E57373 | Error/warning states |
 * | yellows | #FFEB3B | #FFEE58 | #FFF176 | Warning information |
 * | purples | #9C27B0 | #AB47BC | #BA68C8 | Accent colors |
 * | greys | #9E9E9E | #BDBDBD | #E0E0E0 | Neutral text/backgrounds |
 *
 * @see MviColor
 * @see MviColors
 */
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

/**
 * Immutable data class holding typography styles for the application.
 *
 * Typography styles automatically adapt to the device type (phone vs tablet)
 * by providing larger sizes for tablet layouts.
 *
 * @property titlePrimary Main title style (19sp on phone, 38sp on tablet)
 * @property titleSecondary Secondary title style (16sp on phone, 32sp on tablet)
 * @property bodyPrimary Primary body text style (14sp on phone, 28sp on tablet)
 * @property bodySecondary Secondary body text style (12sp on phone, 24sp on tablet)
 * @property spotPrimary Spot/small text style (10sp on phone, 20sp on tablet)
 * @property spotSecondary Secondary spot text style (8sp on phone, 16sp on tablet)
 */
@Immutable
data class MviTypography(
    val titlePrimary: TextStyle,
    val titleSecondary: TextStyle,
    val bodyPrimary: TextStyle,
    val bodySecondary: TextStyle,
    val spotPrimary: TextStyle,
    val spotSecondary: TextStyle,
)

/**
 * Typography configuration for the application.
 *
 * This function returns a [MviTypography] instance with text styles that
 * adapt to the current device type. All text is colored using [MviTheme.colors].
 *
 * ## Device Adaptation
 * The typography automatically adapts to the device size:
 *
 * | Style | Phone | Tablet |
 * |-------|-------|--------|
 * | titlePrimary | 19sp | 38sp |
 * | titleSecondary | 16sp | 32sp |
 * | bodyPrimary | 14sp | 28sp |
 * | bodySecondary | 12sp | 24sp |
 * | spotPrimary | 10sp | 20sp |
 * | spotSecondary | 8sp | 16sp |
 *
 * ## Usage
 * ```kotlin
 * // Within MviTheme
 * MviTheme.typography.titlePrimary
 * MviTheme.typography.bodyPrimary
 *
 * // Direct access (not recommended)
 * mviTypography.titlePrimary
 * ```
 *
 * @see MviTypography
 */
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

/**
 * Extends a color with a default fallback when the color is null or unspecified.
 *
 * Use this when working with nullable colors to ensure a valid color is always
 * returned, preventing crashes from unspecified colors.
 *
 * ## Usage
 * ```kotlin
 * val color = userAvatarColor?.default(Colors.OnSurface)
 * Box(
 *     color = color,
 *     content = { /* ... */ }
 * )
 * ```
 *
 * @param defaultColor The color to use when the input is null or unspecified
 * @return The provided color if valid, otherwise the default color
 *
 * @see Color.Unspecified
 */
fun Color?.default(defaultColor: Color): Color {
    return if (this == null || this == Color.Unspecified) {
        defaultColor
    } else {
        this
    }
}
